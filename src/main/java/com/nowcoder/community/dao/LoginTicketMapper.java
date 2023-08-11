package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketMapper {
    //插入登录凭证
    @Insert("insert into login_ticket (user_id,ticket,status,expired) values (#{userId},#{ticket},#{status},#{expired})")
    //下面这个开启自动生成主键并指定主键为id 并且会自动回填进pojo类中
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket ticket);

    //查询登录凭证
    @Select("select * from login_ticket where ticket = #{ticket}")
    LoginTicket selectByTicket(String ticket);

    //修改登录凭证
    @Update("update login_ticket set status = #{status} where ticket = #{ticket}")
    int updateStatus(String ticket,int status);
}
