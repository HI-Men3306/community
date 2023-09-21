package com.nowcoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    //Caffeine最大缓存数
    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    //Caffeine缓存时间
    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    //Caffeine核心接口: Cache, LoadingCache, AsyncLoadingCache

    //帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    //帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init() {
        // 创建并初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)//最大缓存数量
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)//缓存保存时间
                //创建缓存  从数据库中查询
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    //这里的key是获取缓存的唯一表示
                    public @Nullable List<DiscussPost> load(String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        // 二级缓存: Redis -> mysql

                        logger.debug("load post list from DB.");
                        return discussPostMapper.selectDiscussPost(0, offset, limit, 1);
                    }
                });

        // 创建并初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)//最大缓存数
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)//缓存保存时间
                //创建缓存  从数据库中查询
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(Integer key) throws Exception {
                        logger.debug("load post list from DB.");
                        return discussPostMapper.selectDiscussPostCount(key);
                    }
                });
    }

    //获取帖子列表
    public List<DiscussPost> SelectDiscussPost(int userId, int offset, int limit, int orderMode) {
        //缓存 按照热度排行的帖子数据 即userId为0（默认查询）    orderMode为1（热度排行）

        //当调用方执行相应的方法时，会将需要缓存的数据的相关信息编码到缓存键中，然后传递给缓存对象。
        //缓存对象会根据缓存键来查找对应的缓存数据，如果找到则直接返回，
        //否则执行缓存加载逻辑（即调用CacheLoader的load()方法）来加载数据并更新缓存。
        if (userId == 0 && orderMode == 1) {//从缓存中取出热帖列表
            return postListCache.get(offset + ":" + limit);
        }

        //打印日志 说明当前缓存中不存在 从数据库中查找了
        logger.debug("load post list from DB.");
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPost(userId, offset, limit, orderMode);
        return discussPosts;
    }

    //根据id查询当前用户的帖子条数
    public int SelectCount(int userId) {
        //查询帖子总数是高频操作 所以将其缓存到Caffeine中
        if (userId == 0) {//只有查询所有帖子的数量时才从缓存中查找
            return postRowsCache.get(userId);
        }

        //打印日志 说明当前缓存中不存在 从数据库中查找了
        logger.debug("load post list from DB.");
        int rows = discussPostMapper.selectDiscussPostCount(userId);
        return rows;
    }

    //根据id查询帖子
    public DiscussPost SelectById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    //根据id查找评论
    public Comment findCommentById(int id) {
        return discussPostMapper.findCommentById(id);
    }

    //添加帖子
    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        //对帖子中的HTML标签进行转义  如果发布的帖子内容中存在html标签 可能会对整个页面的布局做出影响
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //对帖子进行过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    //修改帖子的评论数量
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    //修改帖子状态
    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    //修改帖子类型
    public int updateType(int id, int status) {
        return discussPostMapper.updateType(id, status);
    }

    //修改帖子分数
    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }

    //查询帖子状态
    public int selectStatus(int id) {
        return discussPostMapper.selectStatus(id);
    }

    //查询帖子类型
    public int selectType(int id) {
        return discussPostMapper.selectType(id);
    }
}
