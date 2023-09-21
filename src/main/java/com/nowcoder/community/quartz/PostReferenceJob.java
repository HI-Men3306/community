package com.nowcoder.community.quartz;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.redisOperate.RedisUpdateScore;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityConstant;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostReferenceJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostReferenceJob.class);

    @Autowired
    private RedisUpdateScore updateScore;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    // 牛客纪元
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2001-01-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元失败!", e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        BoundSetOperations operations = updateScore.getBoundSetOperations();

        if (operations.size() == 0) {
            logger.info("[任务取消] 没有需要刷新的帖子!");
            return;
        }

        logger.info("[任务开始] 正在刷新帖子分数中 :!" + operations.size());
        while (operations.size() > 0){
            //获取 redis表中存放的所有对应的帖子ip 并更新
            refresh((Integer) operations.pop());
        }
        logger.info("[任务结束] 帖子分数刷新完毕!");
    }

    private void refresh(int postId) {
        //获取对应帖子信息
        DiscussPost discussPost = discussPostService.SelectById(postId);

        //帖子是否被加精
        boolean essence = discussPost.getStatus() == 1;
        //帖子的评论数量
        int commentCount = discussPost.getCommentCount();
        //帖子的点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
        //计算权重
        double w = (essence ? 75 : 0) + commentCount * 10 + likeCount * 2;
        //计算分数  帖子权重 + 距离天数
        double score = Math.log10(Math.max(w, 1))
                + (discussPost.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);
        //更新分数到sql中
        discussPostService.updateScore(postId,score);
        //更新帖子到elasticsearch中
        discussPost.setScore(score);//更新一下最新的分数
        elasticsearchService.saveDiscussPost(discussPost);
    }

}
