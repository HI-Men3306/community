package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    //分页查询数据
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    //查询评论条数
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    //添加评论
    public int addComment(Comment comment){
        //对帖子中的内容进行处理
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));//标签转义
        comment.setContent(sensitiveFilter.filter(comment.getContent()));//敏感词过滤
        return commentMapper.insertComment(comment);
    }

}
