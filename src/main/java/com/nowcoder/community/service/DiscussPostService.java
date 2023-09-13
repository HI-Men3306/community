package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> SelectDiscussPost(int userId,int offset,int limit){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPost(userId, offset, limit);
        return discussPosts;
    }

    //根据id查询当前用户的帖子条数
    public int SelectCount(int userId){
        int rows = discussPostMapper.selectDiscussPostCount(userId);
        return rows;
    }

    public DiscussPost SelectById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    public Comment findCommentById(int id){
        return discussPostMapper.findCommentById(id);
    }

    //添加帖子
    public int addDiscussPost(DiscussPost post){
        if(post == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //对帖子中的HTML标签进行转义  如果发布的帖子内容中存在html标签 可能会对整个页面的布局做出影响
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //对帖子进行过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    //修改帖子的评论数量
    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    //修改帖子状态
    public int updateStatus(int id,int status){
        return discussPostMapper.updateStatus(id,status);
    }

    //修改帖子类型
    public int updateType(int id,int status){
        return discussPostMapper.updateType(id,status);
    }
}
