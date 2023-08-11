package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    //自定义本类日志信息
    static private Logger logger = LoggerFactory.getLogger(LoginController.class);

    //路径前缀
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    //生成验证码
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpSession session, HttpServletResponse response){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session   为什么要存呢？
        //在后面登录时 用于比对输入的验证码是否和生成的验证码一致
        //方便地在后续的请求中获取到之前生成的验证码，以便进行验证。同时，也可以避免每次请求都重新生成验证码，提高系统的性能和效率。
        session.setAttribute("kaptcha",text);

        //为什么这里响应的验证码图片会显示在指定的div位置呢？
        /*
        * 当浏览器接收到 HTTP 响应时，会自动解析响应内容，并根据 HTML 中的 <img> 标签的属性来显示图片。
        * 因此，如果在 HTML 中没有指定 src 属性或者指定了一个无效的 URL,浏览器就会使用默认的图片处理方式，即从当前页面加载图片。
        * */

        //将验证码响应到浏览器
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            logger.error("生成验证码失败！" + e.getMessage());
        }
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        //调用userService进行注册操作
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()){//注册成功
            model.addAttribute("Msg","注册成功，请前往激活！");
            model.addAttribute("Url","/index");//用于页面中的跳转
            //跳转到等待页面
            return "/site/operate-result";
        }else{//注册失败 重新跳转到当前页面
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    // http://localhost:8080/community/activation/101/code
    //当点击邮件中的激活链接后 这里接收过来的发送的请求
    //链接中使用restful风格拼接了两个参数 需要验证激活的用户id  激活码
    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String register(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){
        System.out.println(userId + "  " + code);
        int status = userService.activation(userId, code);
        System.out.println("激活状态" + status);
        if(status == ACTIVATION_FAILURE){
            model.addAttribute("Msg","激活失败！");
            model.addAttribute("Url","/index");
        }else if(status == ACTIVATION_SUCCESS){
            model.addAttribute("Msg","激活成功!");
            model.addAttribute("Url","/login");
        }else{
            System.out.println("激活失败");
            model.addAttribute("Msg","用户已激活，请勿重复激活！");
            model.addAttribute("Url","/index");
        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(String username,String password,String code,boolean rememberMe,
                        Model model,HttpSession session,HttpServletResponse response){
        //验证验证码是否正确
        String kaptcha = (String) session.getAttribute("kaptcha");//获取之前存放在session中的验证码
        if(StringUtils.isBlank(code) || StringUtils.isBlank(kaptcha) || !code.equalsIgnoreCase(kaptcha)){
            model.addAttribute("codeMsg","验证码错误!");
            return "/site/login";
        }
        //用户登录
        //判断用户登录凭证的存在时间
        long expiredSeconds = rememberMe ? REMEMBER_ME_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> res = userService.login(username, password, expiredSeconds);
        if(res.containsKey("ticket")){//res中存在ticket说明用户登录成功 ticket为用户的登录凭证
            //向客户端发送登录凭证 通过cookie  用于后续的传输
            Cookie cookie = new Cookie("ticket",(String) res.get("ticket"));
            cookie.setPath(contextPath);//设置cookie对那些路径请求生效
            cookie.setMaxAge((int)expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{//登录失败
            //传输错误信息
            model.addAttribute("usernameMsg",res.get("usernameMsg"));
            model.addAttribute("passwordMsg",res.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    //@CookieValue注解会从前端cookie中获取名为ticket的cookie对象并赋值给指定参数
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }
}
