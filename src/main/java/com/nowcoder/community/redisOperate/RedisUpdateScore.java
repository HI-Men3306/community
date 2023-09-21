package com.nowcoder.community.redisOperate;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUpdateScore {
    @Autowired
    private RedisTemplate redisTemplate;

    private String RedisKey = RedisKeyUtil.getPostKey();

    //向需要更新分数的帖子库中添加帖子id
    public void insertPostId(int id){
        redisTemplate.opsForSet().add(RedisKey,id);
    }

    public BoundSetOperations getBoundSetOperations(){
        return redisTemplate.boundSetOps(RedisKey);
    }
}
