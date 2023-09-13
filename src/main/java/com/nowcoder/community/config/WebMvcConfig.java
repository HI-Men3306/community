package com.nowcoder.community.config;

import com.nowcoder.community.controller.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//拦截器配置信息
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    //测试用的
    @Autowired
    private AlphaInterceptor alphaInterceptor;

    //统计网站数据用的拦截器
    @Autowired
    private TotalDataInterceptor totalDataInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    //用于非法的路径请求  因为引入了security 所以security完全可以替代该拦截器
    //@Autowired
    //private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private UnreadMessageInterceptor unreadMessageInterceptor;

    /*
    * 拦截器的拦截处理顺序是按照注册的顺序来的
    * 在下面中我先注册了LoginTicket登录凭证的拦截器
    * 再注册了LoginRequired特殊的请求处理的拦截器
    *
    * 且这里的注册顺序不能变  因为LoginRequired依赖LoginTicket中的HostHolder来验证当前用户的登录状态
    * */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg")
                .addPathPatterns("/register","/login");
        //拦截所有的页面请求 因为需要对当前用户登录状态进行判断无论其在什么页面上
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
        //registry.addInterceptor(loginRequiredInterceptor)
        //        .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
        registry.addInterceptor(unreadMessageInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
        registry.addInterceptor(totalDataInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }
}
