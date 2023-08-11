package com.nowcoder.community.service;

import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

@Service
public class LikeService implements CommunityConstant {

    //很奇怪  这里注入的RedisTemplate   他的名字必须为redisTemplate   如果是其他的 如template 会在注入时报错 说有两个可供选择的bean
    @Autowired
    private RedisTemplate redisTemplate;

    //点赞 第一次为点赞  第二次为取消点赞   +    累计当前用户的点赞数量及点赞者
    public void like(int entityType, int entityId, int userId,int entityUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //生成点赞实体对应redis的key
                String key = RedisKeyUtil.getEntityLikeSetKey(entityType, entityId);
                //获取该key对应的集合的操作对象
                BoundSetOperations<String, Object> operations = redisTemplate.boundSetOps(key);
                //生成都有那些用户给当前用户点赞的set集合 key（存放点赞的用户）
                String entityUserLikeKey = RedisKeyUtil.getUserLikeSetKey(entityUserId);
                //获取当前用户key的集合操作对象
                BoundSetOperations<String, Object> userOperation = redisTemplate.boundSetOps(entityUserLikeKey);
                //生成当前用户获得的点赞个数 key （存放获得的点赞个数）
                String userLikeCountKey = RedisKeyUtil.getUserLikeCountKey(entityUserId);
                BoundValueOperations<String, Object> likeCountOperation = redisTemplate.boundValueOps(userLikeCountKey);
                //redis的查询语句要放在事务之外，因为redis的事务的放在一个队列中同时执行的，所以查询语句放在事务中是无效的，要提前查询
                Boolean isMember = operations.isMember(userId);//查询当前用户是否已经点过赞
                //开启redis事务
                redisOperations.multi();
                if(isMember){
                    operations.remove(userId);
                    userOperation.remove(userId);
                    likeCountOperation.decrement();
                }else{
                    operations.add(userId);
                    userOperation.add(userId);
                    likeCountOperation.increment();
                }
                return redisOperations.exec();
            }
        });
    }

    //查询传入实体的点赞数量
    public long findEntityLikeCount(int entityType, int entityId) {
        //生成对应redis的key
        String key = RedisKeyUtil.getEntityLikeSetKey(entityType, entityId);
        BoundSetOperations<String, Object> operations = redisTemplate.boundSetOps(key);
        return operations.size();
    }

    //获取当前登录用户对传入实体的点赞状态 （未点赞，已点赞，踩）
    public int findEntityStatus(int entityType, int entityId, int userId) {
        String key = RedisKeyUtil.getEntityLikeSetKey(entityType, entityId);
        BoundSetOperations<String, Object> operations = redisTemplate.boundSetOps(key);
        return operations.isMember(userId) ? LIKED : UNLIKED;
    }

    //查询某个用户获得的赞的数量
    public int findUserGetLikeCount(int userId){
        String userLikeCountKey = RedisKeyUtil.getUserLikeCountKey(userId);
        Integer count =  (Integer) redisTemplate.opsForValue().get(userLikeCountKey);
        //需要对count进行null 判断   因为如果用户没有收到过赞，那userLikeCountKey这个key是不存在的，所以对应的操作对象也不存在
        //所以返回的值可能为null
        return count == null ? 0 : count;
    }
}
