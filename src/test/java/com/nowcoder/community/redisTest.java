package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisCallback;
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

    @Test
    public void TestOfBigData(){
        //opsForHyperLogLog会对重复的数据进行去重
        String redisKey1 = "redis:gll:1";
        for (int i = 0; i < 100000; i++) {
            template.opsForHyperLogLog().add(redisKey1,i);
            int random = (int) Math.random() * 100000 + 1;
            template.opsForHyperLogLog().add(redisKey1,random);
        }
        System.out.println(template.opsForHyperLogLog().size(redisKey1));
    }

    @Test
    public void TestOfUnion(){
        //opsForHyperLogLog会对重复的数据进行去重
        String redisKey2 = "redis:gll:2";
        String redisKey3 = "redis:gll:3";
        String redisKey4 = "redis:gll:4";
        String redisKeyunion = "redis:gll:union";
        for(int i = 1;i <= 10000;i++){
            template.opsForHyperLogLog().add(redisKey2,i);
        }
        for(int i = 5001;i <= 15000;i++){
            template.opsForHyperLogLog().add(redisKey3,i);
        }
        for(int i = 10001;i <= 20000;i++){
            template.opsForHyperLogLog().add(redisKey4,i);
        }

        //整合多个hll的大小并去重
        template.opsForHyperLogLog().union(redisKeyunion,redisKey2,redisKey3,redisKey4);
        System.out.println(template.opsForHyperLogLog().size(redisKeyunion));
    }

    @Test
    public void TestOfBitMap(){
        String redisKey1 = "redis:bm:1";
        for (int i = 0; i < 3; i++) {
            template.opsForValue().setBit(redisKey1,i,true);
        }
        Object obj = template.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey1.getBytes());
            }
        });
        for (int i = 0; i < 3; i++) {
            System.out.println(template.opsForValue().getBit(redisKey1, i));
        }
        System.out.println(obj);
    }
}
