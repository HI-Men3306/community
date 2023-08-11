package com.nowcoder.community.controller;

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

    //点赞功能
    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId){
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

        return CommunityUtil.getJSONString(CODE_SUCCESS,null,map);
    }

}
