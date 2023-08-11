package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

import static com.nowcoder.community.util.CommunityConstant.NOT_READ;
import static com.nowcoder.community.util.CommunityConstant.READ;

@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    //获取会话列表  其中列表中的每条会话都显示 该会话中的未读私信条数 总的私信条数 私信的用户
    @RequestMapping(path = "/letter/list",method = RequestMethod.GET)
    public String getLettersList(Model model, Page page){
        //获取当前用户信息
        User user = hostHolder.getUser();
        //设置当前分页信息
        page.setLimit(7);
        page.setRows(messageService.findConversationCount(user.getId()));
        page.setPath("/letter/list");
        //当前用户的未读会话数量
        int conversationUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("conversationUnreadCount",conversationUnreadCount);

        //获取会话列表
        List<Message> conversations = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> list = new ArrayList<>();
        for (Message conversation : conversations) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("conversation",conversation);//会话 （最近的一条私信信息）
            map.put("letterCount",messageService.findLetterCount(conversation.getConversationId()));//当前会话的私信数量
            map.put("letterUnreadCount",messageService.findLetterUnreadCount(user.getId(),
                    conversation.getConversationId()));//当前会话未读私信数量
            //获取私信的用户
            int targetId = user.getId() == conversation.getFromId() ? conversation.getToId() : conversation.getFromId();
            User target = userService.SelectUserById(targetId);
            map.put("target",target);
            list.add(map);
        }
        model.addAttribute("list",list);
        return "/site/letter";
    }

    //获取会话中的详细私信列表  并设置当前会话中的未读私信为已读
    @RequestMapping(path = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLitterDetail(@PathVariable("conversationId") String conversationId,Model model,Page page){
        //设置分页数据
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        page.setLimit(5);
        User curUser = hostHolder.getUser();//获取当前登录用户
        //私信列表
        List<Message> letters = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> letterList = new ArrayList<>();
        for (Message letter : letters) {
            //将当前的私信中的未读更新为已读
            if(letter.getStatus() == NOT_READ && curUser.getId() == letter.getToId()){//只有当前私信状态为未读 且当前用户为私信接收的一方 才更新状态
                messageService.setLetterStatus(letter.getId(),READ);
            }
            //获取私信列表数据
            HashMap<String, Object> map = new HashMap<>();
            map.put("letter",letter);
            User letterUser = userService.SelectUserById(letter.getFromId());//获取当前私信的发送用户
            //判断当前发送方是否为当前用户  toUser为当前用户   fromUser对私信用户
            User toUser = letterUser.getId() == curUser.getId() ? curUser : null;
            User fromUser = letterUser.getId() == curUser.getId() ? null : userService.SelectUserById(letterUser.getId());
            map.put("toUser",toUser);
            map.put("fromUser",fromUser);
            letterList.add(map);
        }
        model.addAttribute("letters",letterList);
        //获取当前私信的对象
        Message message = letters.get(0);
        User target = userService.SelectUserById(message.getFromId() == curUser.getId() ? message.getToId() : message.getFromId());
        model.addAttribute("target",target);
        return "/site/letter-detail";
    }

    //发送私信
    @RequestMapping(path = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content){
        if(toName == null || content == null){
            return CommunityUtil.getJSONString(1,"数据错误！");
        }
        User toUser = userService.selectUserByName(toName);//获取发送私信对象用户
        if(toUser == null){
            return CommunityUtil.getJSONString(1,"用户不存在");
        }
        //设置私信
        Message message = new Message();
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(toUser.getId());
        //生成conversationId 私信双方id号小的在前
        String conversationId = toUser.getId() > hostHolder.getUser().getId() ? hostHolder.getUser().getId() + "_" + toUser.getId() : toUser.getId() + "_" + hostHolder.getUser().getId();
        message.setConversationId(conversationId);

        messageService.sendLetter(message);

        return CommunityUtil.getJSONString(0);
    }
}
