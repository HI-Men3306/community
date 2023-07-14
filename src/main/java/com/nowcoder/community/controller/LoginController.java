package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
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
}
