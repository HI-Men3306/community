package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

//弃用
//引入security之后，替代了LoginRequiredInterceptor过滤器
//这个过滤器用于 过滤未登录用户访问敏感资源地址
@Deprecated
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    /*
    * 这个拦截器主要是为了拦截  当当前用户处于未登录状态时，加入其在地址栏直接输入url来发送一些登录状态才有的请求
    * 这个是不允许的 防止其未登录就直接发送请求修改数据
    * 所以我们要对一些敏感的请求进行拦截 判断当发送该请求时，用户状态是否处于合法状态
    * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {//判断拦截的对象是否为一个方法 （防止注解添加错位置）
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);//获取该方法的LoginRequired注解
            if(loginRequired != null && hostHolder.getUser() == null){
                //如果当前controller中有LoginRequired注解 且 当前的用户状态为未登录状态 拦截此次请求 重定向到登录页面
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }

        return true;
    }
}
