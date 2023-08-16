package com.nowcoder.community.controller;

import com.nowcoder.community.Event.EventProducer;
import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    //点赞功能
    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    //@LoginRequired//需要用户登录后才能进行点赞
    public String like(int entityType,int entityId,int entityUserId,int postId){
        User user = hostHolder.getUser();
        //点赞
        likeService.like(entityType,entityId,user.getId(),entityUserId);
        //当前实体的点赞数量
        long entityLikeCount = likeService.findEntityLikeCount(entityType, entityId);
        //当前用户对当前实体的点赞状态
        int entityStatus = likeService.findEntityStatus(entityType, entityId, user.getId());

        HashMap<String, Object> map = new HashMap<>();
        map.put("likeCount",entityLikeCount);
        map.put("likeStatus",entityStatus);

        //如果是点赞   则触发点赞事件  发送站内消息
        if(entityStatus == LIKED){
            Event event = new Event().setTopic(TOPIC_LIKE)
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);//设置该点赞发生的帖子编号  用户后面使用
            //发送消息
            eventProducer.sendEvent(event);
        }
        return CommunityUtil.getJSONString(CODE_SUCCESS,null,map);
    }

}
