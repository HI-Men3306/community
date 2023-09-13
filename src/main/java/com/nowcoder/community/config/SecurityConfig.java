package com.nowcoder.community.config;

import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    //不拦截静态资源
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权  对需要过滤的请求路径和其对应的权限进行配置
        http.authorizeRequests()
                //需要登录才能访问的路径 和 对应的权限
                .antMatchers(
                        "/comment/add/**",
                        "/discuss/add",
                        "/follow",
                        "/unfollow",
                        "/like",
                        "/logout",//TODO
                        "/user/setting",
                        "/user/upload",
                        "/letter/**",
                        "/notice/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_USER,
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR)
                //版主权限 ---> 置顶 帖子设置精华
                .antMatchers(
                        "/discuss/top",
                        "/discuss/essence"
                )
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR)
                //管理员权限  ---> 删除帖子
                .antMatchers(
                        "/discuss/delete",
                        "/data/**"//获取网站访问数量信息
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()//允许其他所有请求通过，不对其进行拦截
                .and().csrf().disable();//不启用csrf防御攻击


        http.exceptionHandling()
                //当用户未登录时要访问被拦截的路径     相应的配置信息
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        //根据请求的方式不同来给出不同的处理
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {//是异步方式发送的请求  要返回json
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "你还没有登录哦!"));
                        } else {//表单发起的请求   直接重定向到登录页面
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                //当用户访问请求时 权限不够   相应的配置
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)) {//是异步方式发送的请求  要返回json
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "你的权限不够!"));
                        } else {//表单发起的请求   直接重定向到权限不够的提示页面上
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });


        // Security底层默认会拦截/logout请求,进行退出处理.
        // 覆盖它默认的逻辑,才能执行我们自己的退出代码.
        http.logout().logoutUrl("/securitylogout");//不存在的一个路径  这样就间接跳过了security的logout拦截
    }
}
