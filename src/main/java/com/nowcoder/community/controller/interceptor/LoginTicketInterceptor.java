package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;

import static com.nowcoder.community.util.CookieUtil.getTicket;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    //每个独立请求都视为一个线程 请求之间独立 存放每次请求的user信息
    @Autowired
    private HostHolder hostHolder;

    //在controller之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取存放域cookie中的登录凭证
        String ticket = getTicket(request, "ticket");
        if (!StringUtils.isBlank(ticket)) {
            //检查登录凭证
            LoginTicket loginTicket = userService.getLoginTicket(ticket);//根据登录凭证获取登录凭证完整信息
            //登录凭证不为空 且 凭证有效  且  凭证没过期
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                //根据登录凭证中的信息获取用户的信息
                User user = userService.SelectUserById(loginTicket.getUserId());
                // 在本次请求中持有用户
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    //controller之后  templateEngine处理之前 向模板共享当前请求中的user信息
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
    }

    //模板处理之后  及时清除ThreadLocal中的数据  避免存储数据过多 造成内存压力
    //所以也就是意味着 每次访问刷新一个页面都要重新从cookie中获取登录凭证 每次页面的关闭或跳转 当前请求线程中的user对象都会被清空
    //即每个页面中的user对象都是重新获取的
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
