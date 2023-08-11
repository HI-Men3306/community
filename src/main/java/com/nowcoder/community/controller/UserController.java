package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.nowcoder.community.util.CookieUtil.getTicket;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;//项目域名

    @Value("${community.path.upload}")
    private String uploadPath;//本地文件存放路径

    @Value("${server.servlet.context-path}")
    private String contextPath;//项目路径

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired//拦截设置页面请求
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired//拦截上传页面请求
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        //判断图片是否为空
        if(headerImage == null){
            model.addAttribute("error","文件不能为空！");
            return "/site/setting";
        }
        //获取传输过来的图片类型后缀
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);//截取.后面的文件后缀名
        //判断图片类型是否合法
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件类型错误！");
            return "/site/setting";
        }
        //重新生成图片名
        filename = CommunityUtil.GenerateUUID() + "." + suffix;

        System.out.println("文件名为" + filename);

        //图片存放到本地
        File des = new File(uploadPath + "/" + filename);//确定文件存放位置
        try {
            headerImage.transferTo(des);//保存文件
        } catch (IOException e) {
            logger.error("文件上传失败" + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }

        //更新对应用户的头像地址
        Integer id = hostHolder.getUser().getId();//从hostHolder中获取当前用户id
        //根据上传的图片创建一个新的URL地址
        // http://localhost:8080/community/user/header/xxx.png
        String URL = domain + contextPath + "/user/header/" + filename;
        userService.updateHeaderUrl(id,URL);

        return "redirect:/index";
    }

    /*
    * 这个是什么？   在uploadHeader方法中我们更新了用户的头像Url地址
    * 其实图片的Url地址也是一个web请求，只不过我们之前使用的是网图，当我们的请求发送到他们的服务器上时，他们的服务器会自动响应对应的图片
    * 并直接渲染到发送请求的页面上
    *
    * 当我们上传图片之后，因为图片保存在我们本地上，服务器就在我们本地，这个webUrl的请求就会发送到本地服务器上
    * 所以就需要我们自己来处理对应的url请求  将图片响应到发送请求的页面上
    * */
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    @LoginRequired//拦截更新密码请求
    @RequestMapping(path = "/updatePsd",method = RequestMethod.POST)
    public String updatePassword(Model model, String OriginPassword, String NewPassword, HttpServletRequest request){
        if(StringUtils.isBlank(OriginPassword) || StringUtils.isBlank(NewPassword)){
            model.addAttribute("OriginPsd","密码不能为空");
            return "/site/setting";//重新输入信息
        }
        //验证原密码是否正确
        User user = hostHolder.getUser();//获取当前线程中的用户信息
        String salt = user.getSalt();//获取当前用户的加盐信息
        if(!CommunityUtil.md5(OriginPassword + salt).equals(user.getPassword())){//传入旧密码的加密结果和查询到的结果不一致
            model.addAttribute("OriginPsd","原密码错误！");
            return "/site/setting";//重新输入信息
        }
        //判断旧密码和新密码是否一致
        if(OriginPassword.equals(NewPassword)){
            model.addAttribute("NewPsd","新密码不能与旧密码一致！");
            return "/site/setting";//重新输入信息
        }

        //修改密码
        userService.updatePassword(user.getId(),NewPassword);
        //在跳转到登录页面之前要将当前用户退出登录  即使当前页面的登录状态失效
        String ticket = getTicket(request, "ticket");//因为登录凭证的信息一直存放在cookie中 所以可以直接获取
        userService.logout(ticket);//使当前用户的登录凭证失效

        return "redirect:/login";
        //重定向到登录页面  为什么不能直接用转发？
        //转发只有一次请求，重定向有两次请求。 所以说转发不会重新发送请求 也就意味着页面的数据不会刷新
        //而使用重定向 页面会重新发送请求 页面上的数据会重新刷新
    }

    @LoginRequired//拦截更新密码请求
    @RequestMapping(path = "/updatePsd",method = RequestMethod.GET)
    public String test(Model model, String OriginPassword, String NewPassword, HttpServletRequest request){
        if(StringUtils.isBlank(OriginPassword) || StringUtils.isBlank(NewPassword)){
            model.addAttribute("OriginPsd","密码不能为空");
            return "/site/setting";//重新输入信息
        }
        //验证原密码是否正确
        User user = hostHolder.getUser();//获取当前线程中的用户信息
        String salt = user.getSalt();//获取当前用户的加盐信息
        if(!CommunityUtil.md5(OriginPassword + salt).equals(user.getPassword())){//传入旧密码的加密结果和查询到的结果不一致
            model.addAttribute("OriginPsd","原密码错误！");
            return "/site/setting";//重新输入信息
        }
        //判断旧密码和新密码是否一致
        if(OriginPassword.equals(NewPassword)){
            model.addAttribute("NewPsd","新密码不能与旧密码一致！");
            return "/site/setting";//重新输入信息
        }

        //修改密码
        userService.updatePassword(user.getId(),NewPassword);
        //在跳转到登录页面之前要将当前用户退出登录  即使当前页面的登录状态失效
        String ticket = getTicket(request, "ticket");//因为登录凭证的信息一直存放在cookie中 所以可以直接获取
        userService.logout(ticket);//使当前用户的登录凭证失效

        return "redirect:/login";
        //重定向到登录页面  为什么不能直接用转发？
        //转发只有一次请求，重定向有两次请求。 所以说转发不会重新发送请求 也就意味着页面的数据不会刷新
        //而使用重定向 页面会重新发送请求 页面上的数据会重新刷新
    }

    //获取个人主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        if(userService.SelectUserById(userId) == null){
            new RuntimeException("用户不存在！");
        }
        long userGetLikeCount = likeService.findUserGetLikeCount(userId);
        model.addAttribute("likeCount",userGetLikeCount);
        model.addAttribute("user",userService.SelectUserById(userId));

        long followCount = followService.getFollowCount(ENTITY_TYPE_USER, userId);
        long fansCount = followService.getFansCount(ENTITY_TYPE_USER, userId);
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){//当前用户处于登录状态
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("followCount",followCount);
        model.addAttribute("fansCount",fansCount);
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }
}
