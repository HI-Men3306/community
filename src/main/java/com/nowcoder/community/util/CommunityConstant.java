package com.nowcoder.community.util;

public interface CommunityConstant {

    //激活成功
    int ACTIVATION_SUCCESS = 0;
    //重复激活
    int ACTIVATION_REPEAT = 1;
    //激活失败
    int ACTIVATION_FAILURE = 2;

    //默认状态的登录凭证超时时间     12小时
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    //记住状态下的登录凭证超时时间  三个月
    int REMEMBER_ME_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * 实体类型: 帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型: 评论
     */
    int ENTITY_TYPE_COMMENT = 2;
    /**
     * 实体类型: 评论
     */
    int ENTITY_TYPE_USER = 3;

    //私信状态为未读
    int NOT_READ = 0;

    //私信状态为已读
    int READ = 1;

    //私信状态为拉黑删除
    int LETTER_BLACK = 2;

    //返回JSON数据中的code  响应成功
    int CODE_SUCCESS = 0;
    //返回JSON数据中的code  响应异常
    int CODE_FAILED = 1;

    //点赞状态 为已赞
    int LIKED = 1;
    //点赞状态 为未赞
    int UNLIKED = 0;
    //点赞状态 为拉黑
    int LIKE_BLACK = 2;

    /**
     * 主题: 评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题: 点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题: 关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 主题: 发帖
     */
    String TOPIC_PUBLISH = "publish";

    /**
     * 系统用户ID
     */
    int SYSTEM_USER_ID = 1;
}
