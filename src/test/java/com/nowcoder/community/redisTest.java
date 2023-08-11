package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class redisTest {
    @Autowired
    private RedisTemplate<String,Object> template;

    @Test
    public void StringTestOfRedis(){
        String RedisKey = "test:count";
        ValueOperations<String, Object> operations = template.opsForValue();
        operations.set(RedisKey,1);
        System.out.println(operations.get(RedisKey));
        System.out.println(operations.increment(RedisKey));
        System.out.println(operations.decrement(RedisKey));
    }
}
