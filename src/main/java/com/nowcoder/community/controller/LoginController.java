package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "register",method = RequestMethod.POST)
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
}
