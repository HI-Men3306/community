package com.nowcoder.community.controller;

import com.nowcoder.community.Event.EventProducer;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    //为什么要携带一个discussPostId？   因为添加成功评论之后，我们要重定向到帖子详情页面  而显示帖子详情controller需要帖子的id
    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        //对comment数据进行处理
        comment.setUserId(hostHolder.getUser().getId());//设置评论的作者id  评论的作者就是当前登录的用户
        comment.setStatus(0);//设置当前评论状态
        comment.setCreateTime(new Date());

        //插入评论
        commentService.addComment(comment);
        //更新帖子的评论数量
        if(comment.getEntityType() == ENTITY_TYPE_POST){//只有当前的评论是针对帖子的才更新帖子的评论数量
            //查询添加评论后 该帖子下有多少评论
            int commentCount = commentService.findCommentCount(comment.getEntityType(), comment.getEntityId());
            //更新帖子的评论数
            discussPostService.updateCommentCount(comment.getEntityId(),commentCount);
        }

        //触发评论事件 发送系统通知信息
        Event event = new Event().setTopic(TOPIC_COMMENT)//设置事件主题
                .setEntityType(comment.getEntityType())//设置事件的实体类型
                .setEntityId(comment.getEntityId())//设置事件的实体id
                .setData("postId",discussPostId)//设置评论的帖子编号 用于显示
                .setUserId(hostHolder.getUser().getId());//设置触发事件的用户id
        //设置事件的发送对象id
        if(comment.getEntityType() == ENTITY_TYPE_POST){//评论对象为帖子
            DiscussPost target = discussPostService.SelectById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());//设置要发送系统私信的用户id
        }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){//评论对象为用户
            Comment target = discussPostService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());//设置要发送系统私信的用户id
        }
        eventProducer.sendEvent(event);


        //如果评论的对象是帖子   更新帖子在elasticsearch中的状态
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)//事件主题为 发布
                    .setUserId(comment.getUserId())//帖子作者id
                    .setEntityType(ENTITY_TYPE_POST)//事件实体类型为 帖子
                    .setEntityId(discussPostId);//帖子编号
            eventProducer.sendEvent(event);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
