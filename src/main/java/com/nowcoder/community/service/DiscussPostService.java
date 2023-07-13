package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

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
}
