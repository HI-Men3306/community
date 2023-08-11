package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前页的会话列表
    List<Message> selectConversations(int userId,int offset,int limit);
    //查询用户总的会话条数
    int selectConversationCount(int userId);
    //查询指定会话的当前页私信列表
    List<Message> selectLetters(String conversationId,int offset,int limit);
    //查询指定会话的总的私信条数
    int selectLetterCount(String conversationId);
    //查询未读的会话或私信
    int selectLetterUnreadCount(int userId,String conversationId);
    //添加私信
    @Insert("insert into message (from_id,to_id,conversation_id,content,status,create_time) " +
            "values (#{fromId},#{toId},#{conversationId},#{content},#{status},#{createTime})")
    int addLetter(Message message);

    //设置私信状态 （已读 or 删除）
    @Update("update message set status = #{status} where id = #{id}")
    int setLetterStatus(int id,int status);
}
