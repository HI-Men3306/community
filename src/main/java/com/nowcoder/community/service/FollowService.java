package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    //关注某个实体
    public void follow(int entityType,int entityId,int userId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, userId);//粉丝
                String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);//关注实体
                operations.multi();
                BoundZSetOperations<String, Object> followeeOperation = redisTemplate.boundZSetOps(followeeKey);
                BoundZSetOperations<String, Object> followerOperation = redisTemplate.boundZSetOps(followerKey);
                followerOperation.add(entityId,System.currentTimeMillis());
                followeeOperation.add(userId,System.currentTimeMillis());
                return operations.exec();
            }
        });
    }
    //取关某个实体
    public void unfollow(int entityType,int entityId,int userId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, userId);//粉丝
                String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);//关注实体
                operations.multi();
                BoundZSetOperations<String, Object> followeeOperation = redisTemplate.boundZSetOps(followeeKey);
                BoundZSetOperations<String, Object> followerOperation = redisTemplate.boundZSetOps(followerKey);
                followerOperation.remove(entityId);
                followeeOperation.remove(userId);
                return operations.exec();
            }
        });
    }

    //查看某个实体的粉丝数量
    public long getFansCount(int entityType,int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);//关注实体
        BoundZSetOperations<String, Object> operations = redisTemplate.boundZSetOps(followeeKey);
        Long size = operations.zCard();
        return size;
    }

    //查看某个用户的关注数量
    public long getFollowCount(int entityType,int userId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, userId);
        BoundZSetOperations<String, Object> operations = redisTemplate.boundZSetOps(followerKey);
        return operations.zCard();
    }

    //查看用户是否关注过指定的实体
    public boolean hasFollowed(int userId,int entityType,int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
        return redisTemplate.boundZSetOps(followeeKey).score(userId) != null;
    }

    //查询某个用户的关注列表
    //每个map中存放 关注的对象及 关注的时间
    public List<Map<String,Object>> getUserFolloweeList(int userId, int offset, int limit){
        List<Map<String,Object>> list = new ArrayList<>();

        String followeeKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);//生成关注用户的redis key
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);//关注的用户id集合
        if(targetIds == null)
            return null;
        for (Integer targetId : targetIds) {
            Map<String,Object> map = new HashMap<>();
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);//获取关注的用户的关注时间
            User targetUser = userService.SelectUserById(targetId);//获取关注的用户信息
            map.put("followTime",new Date(score.longValue()));
            map.put("targetUser",targetUser);
            list.add(map);
        }
        return list;
    }

    //查询某个用户的粉丝
    //每个map中存放 关注的对象及 关注的时间
    public List<Map<String,Object>> getUserFansList(int userId,int offset,int limit){
        List<Map<String,Object>> list = new ArrayList<>();

        String followeeKey = RedisKeyUtil.getFolloweeKey(ENTITY_TYPE_USER, userId);//生成关注用户的redis key
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);//关注的用户id集合
        if(targetIds == null)
            return null;
        for (Integer targetId : targetIds) {
            Map<String,Object> map = new HashMap<>();
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);//获取关注的用户的关注时间
            User targetUser = userService.SelectUserById(targetId);//获取关注的用户信息
            map.put("followTime",new Date(score.longValue()));
            map.put("targetUser",targetUser);
            list.add(map);
        }
        return list;
    }
}
