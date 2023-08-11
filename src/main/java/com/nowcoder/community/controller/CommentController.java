package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
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
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
