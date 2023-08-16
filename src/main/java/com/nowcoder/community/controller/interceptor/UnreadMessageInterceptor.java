package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//这个过滤器用于 显示当前用户的未读消息数量
@Component
public class UnreadMessageInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    //controller之后   模板之前   用于向页面传递当前用户的总未读消息数量
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user == null || modelAndView == null){
            return;
        }
        int noticeCount = messageService.selectTopicUnreadSystemNoticeCount(user.getId(), null);
        int letterCount = messageService.findLetterUnreadCount(user.getId(), null);
        //向model中添加总的未读消息数量
        modelAndView.addObject("totalUnreadCount",noticeCount + letterCount);
    }
}
