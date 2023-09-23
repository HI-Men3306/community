package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

//用于从request域中获取存放着登录凭证的cookie中的登录凭证
public class CookieUtil {
    public static String getTicket(HttpServletRequest request,String name){
        if(request == null || name == null){
            throw new IllegalArgumentException("获取登录凭证参数为空");
        }
        Cookie[] cookies = request.getCookies();
        //这里一定要判断cookies 不为空  因为可能存在cookies一个都没有
        //例如使用jMeter进行测试时，发送的请求就不携带任何cookie
        if(cookies != null)
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        return null;
    }
}
