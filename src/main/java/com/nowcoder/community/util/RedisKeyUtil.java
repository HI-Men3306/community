package com.nowcoder.community.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE_SET = "likeSet:entity";
    private static final String PREFIX_USER_ID_SET = "likeSet:user";
    private static final String PREFIX_USER_ID_COUNT = "likeCount:user";
    private static final String PREFIX_FOLLOWER_ID_ZSET = "follower:set";
    private static final String PREFIX_FOLLOWEE_ID_ZSET = "followee:set";

    //某个实体的赞
    //like:entity:entityType:entityId -> set(userId)存放的是该实体类对应的点赞用户id集合
    public static String getEntityLikeSetKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE_SET + SPLIT + entityType + SPLIT + entityId;
    }

    //都谁给当前用户点赞了  （存放点赞用户的集合）
    public static String getUserLikeSetKey(int userId){
        return PREFIX_USER_ID_SET + SPLIT + userId;
    }

    //当前用户的获得点赞个数
    public static String getUserLikeCountKey(int userId){
        return PREFIX_USER_ID_COUNT + SPLIT + userId;
    }

    //关注者 （粉丝） 是一个有序集合    作为粉丝
    //follower:set:userId:entityType  ->zset(entityId,time)
    //关注者的关注列表只需要存储关注的实体对象id即可   所以对应的key名中应该包含 关注实体的类型 关注者的id
    public static String getFollowerKey(int entityType,int userId){
        return PREFIX_FOLLOWER_ID_ZSET + SPLIT + userId + SPLIT + entityType;
    }

    //实体拥有的粉丝  作为博主
    //followee:entityType:entityId ->zset(userId,time)
    //实体的粉丝列表只需要存储粉丝的id即可  所以对应的key中应该包含 当前实体的类型 和 实体的id
    public static String getFolloweeKey(int entityType,int entityId){
        return PREFIX_FOLLOWEE_ID_ZSET + SPLIT + entityType + SPLIT + entityId;
    }
}