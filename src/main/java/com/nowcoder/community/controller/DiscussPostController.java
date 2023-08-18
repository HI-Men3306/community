package com.nowcoder.community.controller;

import com.nowcoder.community.Event.EventProducer;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
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

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    //添加帖子
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        //获取当前发帖用户 并判断当前登录状态
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "用户未登录!");
        }
        DiscussPost discuss = new DiscussPost();
        discuss.setContent(content);
        discuss.setTitle(title);
        discuss.setUserId(user.getId());
        discuss.setCreateTime(new Date());
        discussPostService.addDiscussPost(discuss);

        //添加帖子之后 触发事件 将新发布的帖子使用kafka 添加到elasticsearch中
        Event event = new Event().setEntityId(discuss.getId())//存放帖子id
                .setUserId(user.getId())//作者id
                .setEntityType(ENTITY_TYPE_POST)//事件类型为 帖子
                .setTopic(TOPIC_PUBLISH);//事件主题为 发布
        eventProducer.sendEvent(event);

        //报错的情况统一后续统一进行处理
        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    //展示帖子详情
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        //获取帖子
        DiscussPost post = discussPostService.SelectById(discussPostId);
        //获取作者
        User user = userService.SelectUserById(post.getUserId());
        //添加到model中
        model.addAttribute("post", post);
        model.addAttribute("user", user);

        //查询当前帖子的点赞数，并判断当前登录用户的点赞状态
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());//点赞数量
        //查询当前登录用户对该用户的点赞状态
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityStatus(ENTITY_TYPE_POST,post.getId(),hostHolder.getUser().getId());
        //添加到model中
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likeStatus", likeStatus);

        //设置分页信息
        page.setLimit(5);//设置展示的评论条数
        page.setPath("/discuss/detail/" + discussPostId);//用于当需要展示翻页信息时，重新向当前路径发送请求用
        //为什么要携带着discussPostId?
        //主要还是要看分页查询的请求路径 看路径需不需要携带参数
        //因为我发送请求的路径controller为/detail/{discussPostId}，我需要携带着当前帖子的编号用来查询数据
        page.setRows(post.getCommentCount());//设置当前帖子中的总评论数

        //评论 ->给帖子的评论
        //回复 ->给评论的评论

        //帖子详情页面中上面展示的帖子的信息  下面还要展示帖子评论信息  而评论中存在其他人对该评论发布的评论
        //所以要分页传输帖子的评论  在每个评论中还要携带该评论所包含的回复信息
        //对于帖子的评论包含：评论信息，作者信息，回复的数量，回复信息   而回复信息中又包含：回复的评论信息，回复的作者信息，回复的对象
        //对于总的分页信息使用list封装  单个的评论信息为使评论、作者、回复信息一一对应 使用map封装

        //评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());

        //向页面传输的评论VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        //向VO列表中存放数据
        for (Comment comment : commentList) {
            //存放单个评论对象
            Map<String, Object> commentVO = new HashMap<>();
            //评论信息
            commentVO.put("comment", comment);
            //评论的作者
            commentVO.put("user", userService.SelectUserById(comment.getUserId()));
            //该评论的回复总数   要查询该评论有多少条回复  ENTITY_TYPE_COMMENT为要查询的评论类型为回复
            //comment.getId()为当前评论的id  为什么是当前评论的id？ 回复中的entity_id代表该类型的评论所属目标的id编号
            //这里要查询的评论的回复  所以回复的所属对象就是该评论  所以这里要为该评论的id
            commentVO.put("replyCount", commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId()));
            //查询当前评论的点赞数 并判断当前登录用户的点赞状态
            commentVO.put("VOLikeCount",likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId()));//点赞数
            int VOLikeStatus = hostHolder.getUser() == null ? 0 :
                    likeService.findEntityStatus(ENTITY_TYPE_COMMENT,comment.getId(),hostHolder.getUser().getId());
            commentVO.put("VOLikeStatus",VOLikeStatus);//当前用户点赞状态


            //存放该评论的所有回复信息
            List<Map<String, Object>> replyVOList = new ArrayList<>();
            //查询出的该评论对应的回复  这里的查询语句本来是分页查询的  但是对于这里的评论中的回复 我们不进行分页查询，直接所有回复全部展示出来
            List<Comment> replyList = commentService.findCommentsByEntity(
                    ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
            //存放回复数据
            for (Comment reply : replyList) {
                //存放单个回复对象
                Map<String, Object> replyVO = new HashMap<>();
                //回复信息
                replyVO.put("reply", reply);
                //回复的作者
                replyVO.put("replyUser", userService.SelectUserById(reply.getUserId()));
                //回复的目标对象  如果向指定的人回复 存在该属性
                User target = reply.getTargetId() == 0 ? null : userService.SelectUserById(reply.getTargetId());
                replyVO.put("target", target);
                //查询回复的点赞数 并判断当前登录用户的点赞状态
                replyVO.put("replyLikeCount",likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId()));//当前回复的点赞数
                int replyLikeStatus = hostHolder.getUser() == null ? 0:
                        likeService.findEntityStatus(ENTITY_TYPE_COMMENT,reply.getId(),hostHolder.getUser().getId());
                replyVO.put("replyLikeStatus",replyLikeStatus);//当前用户点赞状态

                //将回复添加进回复列表中
                replyVOList.add(replyVO);
            }
            //将回复列表添加进评论中
            commentVO.put("replyList", replyVOList);

            //将当前评论添加进评论列表中
            commentVoList.add(commentVO);
        }

        //将评论列表添加到model中
        model.addAttribute("commentList", commentVoList);

        return "/site/discuss-detail";
    }
}
