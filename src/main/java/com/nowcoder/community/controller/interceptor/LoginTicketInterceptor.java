package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.PasswordAuthentication;
import java.util.Date;

import static com.nowcoder.community.util.CookieUtil.getTicket;

//这个过滤器用于 登录当前用户
//用于从cookie中获取当前已经登录过的用户   并根据其凭证信息进行登录
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    //每个独立请求都视为一个线程 请求之间独立 存放每次请求的user信息
    @Autowired
    private HostHolder hostHolder;

    //在controller之前
    @Override
    //用于 记住用户的自动登录
    //被记住的用户会在浏览器中存放一个cookie凭证  根据这个凭证信息来进行用户的自动登录
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

                //因为我们使用的是自己的认证逻辑 跳过了security的认证   但是我们还是需要security的授权功能
                //所以在每次用户登录时，就需要构建用户的认证结果 并将其存入SecurityContext中，以便Security后续进行授权操作
                //构建认证结果
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), userService.getAuthorities(user.getId()));
                //将认证结果存入SecurityContext中
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }
        return true;
    }

    //controller之后  templateEngine处理之前 向模板共享当前请求中的user信息
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    //模板处理之后  及时清除ThreadLocal中的数据  避免存储数据过多 造成内存压力
    //所以也就是意味着 每次访问刷新一个页面都要重新从cookie中获取登录凭证 每次页面的关闭或跳转 当前请求线程中的user对象都会被清空
    //即每个页面中的user对象都是重新获取的
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清理保存的当前用户数据
        hostHolder.clear();
        //清理认证的结果
        SecurityContextHolder.clearContext();
    }
}
