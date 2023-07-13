package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TheNewMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Test
    public void TestOfDiscussPostOfSelectById(){
        DiscussPost discussPost = discussPostService.SelectById(109);
        System.out.println(discussPost);
    }

    @Test
    public void TestOfService(){
        List<DiscussPost> discussPosts = discussPostService.SelectDiscussPost(101, 0, 100);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }
        int i = discussPostService.SelectCount(101);
        System.out.println(i);

        User user = userService.SelectUserById(101);
        System.out.println(user);
    }


    @Test
    public void TestOfDiscussPost(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPost(0, 0, 10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }
        System.out.println();
        int i = discussPostMapper.selectDiscussPostCount(0);
        System.out.println(i);
    }


    @Test
    public void TestOfSelectById(){
        User user = userMapper.selectById(1);
        System.out.println(user);
    }

}
