package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.TotalDataService;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
public class TotalDataInterceptor implements HandlerInterceptor {
    @Autowired
    private TotalDataService dataService;

    @Autowired
    private HostHolder hostHolder;

    //每当有请求发送时就统计网站的访问情况
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求发送用户的ip
        String ipAddress = request.getRemoteAddr();
        //获取用户 如果登录的话
        User user = hostHolder.getUser();

        //记录UV （访问量）
        dataService.recordUV(ipAddress);

        //记录DAU （日活量）
        if(!Objects.isNull(user)){
            dataService.recordDAU(user.getId());
        }
        return true;
    }
}
