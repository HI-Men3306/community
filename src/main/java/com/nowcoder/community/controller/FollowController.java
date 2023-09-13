package com.nowcoder.community.controller;

import com.nowcoder.community.Event.EventProducer;
import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    //关注
    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired//登录验证 如果未登录 进行拦截
    public String follow(int entityType,int entityId,Model model){
        User user = hostHolder.getUser();//获取当前登录用户
        followService.follow(entityType,entityId,user.getId());

        //触发关注事件 发送系统消息通知
        Event event = new Event().setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);

        //发送系统消息
        eventProducer.sendEvent(event);
        return CommunityUtil.getJSONString(CODE_SUCCESS,"关注成功");
    }

    //取关
    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired//登录验证 如果未登录 进行拦截
    public String unfollow(int entityType,int entityId,Model model){
        User user = hostHolder.getUser();//获取当前登录用户
        followService.unfollow(entityType,entityId,user.getId());
        return CommunityUtil.getJSONString(CODE_SUCCESS,"取关成功");
    }

    //获取用户的关注列表 基于分页
    @RequestMapping(path = "/getFollower/{userId}",method = RequestMethod.GET)
    public String getFollower(@PathVariable("userId") int userId, Model model, Page page){
        //设置分页信息
        page.setLimit(5);
        page.setRows((int)followService.getFollowCount(ENTITY_TYPE_USER,userId));
        page.setPath("/getFollower/" + userId);

        //查询当前要查询的用户信息  用来向页面显示查询的用户信息
        User currentUser = userService.SelectUserById(userId);
        model.addAttribute("user",currentUser);

        //查询指定用户的关注列表
        List<Map<String, Object>> followeeList = followService.getUserFolloweeList(userId, page.getOffset(), page.getLimit());
        for (Map<String, Object> map : followeeList) {
            User targetUser = (User)map.get("targetUser");//获取每条数据中的user对象
            //查询当前登录的用户是否关注过该对象
            boolean hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, targetUser.getId());
            map.put("hasFollowed",hasFollowed);
        }
        model.addAttribute("followeeList",followeeList);

        return "/site/followee";
    }


    //获取用户的粉丝列表 基于分页
    @RequestMapping(path = "/getFans/{userId}",method = RequestMethod.GET)
    public String getFans(@PathVariable("userId") int userId, Model model, Page page){
        //设置分页信息
        page.setLimit(5);
        page.setRows((int)followService.getFansCount(ENTITY_TYPE_USER,userId));
        page.setPath("/getFollower/" + userId);

        //查询当前要查询的用户信息  用来向页面显示查询的用户信息
        User currentUser = userService.SelectUserById(userId);
        model.addAttribute("user",currentUser);

        //查询指定用户的关注列表
        List<Map<String, Object>> fansList = followService.getUserFansList(userId, page.getOffset(), page.getLimit());
        for (Map<String, Object> map : fansList) {
            User targetUser = (User)map.get("targetUser");//获取每条数据中的user对象
            //查询当前登录的用户是否关注过该对象
            boolean hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, targetUser.getId());
            map.put("hasFollowed",hasFollowed);
        }
        model.addAttribute("fansList",fansList);

        return "/site/follower";
    }
}
