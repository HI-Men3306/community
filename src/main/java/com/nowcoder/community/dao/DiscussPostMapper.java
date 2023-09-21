package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    //根据id进行分页查询
    //当传入的id为0时代表默认查询 在xml文件中使用动态sql进行判断查询
    List<DiscussPost> selectDiscussPost(int userId,int offset,int limit,int orderMode);

    //根据id查询帖子的数量
    //同样的，在xml文件中使用动态sql进行判断查询
    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名
    int selectDiscussPostCount(@Param("userId") int userId);

    //根据id查询
    @Select("select * from discuss_post where id = #{id}")
    DiscussPost selectDiscussPostById(@Param("id") int id);

    //插入帖子
    @Insert({"insert into discuss_post (user_id,title,content,type,status,create_time,comment_count,score) values" +
            "(#{userId},#{title},#{content},#{type},#{status},#{createTime},#{commentCount},#{score})"})
    @Options(useGeneratedKeys=true,keyProperty="id")
    int insertDiscussPost(DiscussPost post);

    //更新帖子的评论数 id为修改帖子的id commentCount为修改值
    @Update("update discuss_post set comment_count  = #{commentCount} where id = #{id}")
    int updateCommentCount(int id,int commentCount);

    //根据id查询评论
    @Select("select * from comment where id = #{id}")
    Comment findCommentById(int id);

    //查询所有的帖子id
    @Select("select id from discuss_post")
    List<Integer> selectAllDiscussPostId();

    //修改帖子状态
    @Update("update discuss_post set status = #{status} where id = #{id}")
    int updateStatus(int id, int status);

    //修改帖子类型
    @Update("update discuss_post set type = #{type} where id = #{id}")
    int updateType(int id, int type);

    //修改帖子分数
    @Update("update discuss_post set score = #{score} where id = #{id}")
    int updateScore(int id, double score);

    //查询帖子类型是置顶还是非置顶
    @Select("select type from discuss_post where id = #{id}")
    int selectType(int id);

    //查询帖子状态是加精 还是 拉黑
    @Select("select status from discuss_post where id = #{id}")
    int selectStatus(int id);
}
