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
     * 主题: 删除
     */
    String TOPIC_DELETE = "delete";

    /**
     * 系统用户ID
     */
    int SYSTEM_USER_ID = 1;


    /**
     * 权限: 普通用户
     */
    String AUTHORITY_USER = "user";

    /**
     * 权限: 管理员
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     * 权限: 版主
     */
    String AUTHORITY_MODERATOR = "moderator";

    /**
     * 帖子类型:置顶
     */
    int DISCUSS_STATUS_TOP = 1;

    /**
     * 帖子类型:普通
     */
    int DISCUSS_STATUS_COMMON = 0;

    /**
     * 帖子状态：正常
     */
    int DISCUSS_TYPE_COMMON = 0;

    /**
     * 帖子状态：精华
     */
    int DISCUSS_TYPE_ESSENCE = 1;

    /**
     * 帖子状态：拉黑
     */
    int DISCUSS_TYPE_DELETE = 2;
}
