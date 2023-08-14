package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    //查询当前分页的会话列表
    public List<Message> findConversations(int userId,int offset,int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }

    //查询当前用户的总会话数
    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    //查询指定会话的当前分页私信列表
    public List<Message> findLetters(String conversationId,int offset,int limit){
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    //查询指定会话的总的私信数
    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    //查询用户未读的会话数  或  指定会话的未读私信数
    public int findLetterUnreadCount(int userId,String conversationId){
        return messageMapper.selectLetterUnreadCount(userId,conversationId);
    }

    //发送私信
    public int sendLetter(Message message){
        if(message != null){
            //对私信内容进行处理
            message.setContent(HtmlUtils.htmlEscape(message.getContent()));
            message.setContent(sensitiveFilter.filter(message.getContent()));
            return messageMapper.addLetter(message);
        }
        return 0;
    }

    //设置私信状态
    public int setLetterStatus(int id,int status){
        return messageMapper.setLetterStatus(id,status);
    }
}
