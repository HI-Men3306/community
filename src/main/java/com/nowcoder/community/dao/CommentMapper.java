package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {
    //根据评论类型 和 评论对象的id 起始条数 和 查询条数 查询进行分页查询
    @Select("select * from comment where entity_type = #{entityType} and entity_id = #{entityId}" +
            " order by create_time asc limit #{offset}, #{limit}")
    List<Comment> selectCommentByEntity(int entityType,int entityId,int offset,int limit);

    //根据评论类型 和 评论对象的id查询评论数量
    @Select("select count(id) from comment where status = 0 and entity_type = #{entityType} and entity_id = #{entityId}")
    int selectCountByEntity(int entityType,int entityId);

    //添加评论
    @Insert("insert into comment (user_id,entity_type,entity_id,target_id,content,status,create_time) values " +
            "(#{userId},#{entityType},#{entityId},#{targetId},#{content},#{status},#{createTime})")
    int insertComment(Comment comment);
}
