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
    List<DiscussPost> selectDiscussPost(int userId,int offset,int limit);

    //根据id查询帖子的数量
    //同样的，在xml文件中使用动态sql进行判断查询
    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名
    int selectDiscussPostCount(@Param("userId") int userId);

    //根据id查询
    @Select("select * from discuss_post where id = #{id}")
    DiscussPost selectDiscussPostById(@Param("id") int id);

    //插入帖子
    @Insert("insert into discuss_post (user_id,title,content,type,status,create_time,comment_count,score) values" +
            "(#{userId},#{title},#{content},#{type},#{status},#{createTime},#{commentCount},#{score})")
    int insertDiscussPost(DiscussPost post);

    //更新帖子的评论数 id为修改帖子的id commentCount为修改值
    @Update("update discuss_post set comment_count  = #{commentCount} where id = #{id}")
    int updateCommentCount(int id,int commentCount);

    @Select("select * from comment where id = #{id}")
    Comment findCommentById(int id);
}
