package com.nowcoder.community;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.util.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoginTicketTest {
    @Autowired
    private LoginTicketMapper mapper;

    @Test
    public void testOfLoginTicketMapper() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(CommunityUtil.GenerateUUID());
        loginTicket.setExpired(new Date());
        loginTicket.setId(12);
        loginTicket.setUserId(101);
        loginTicket.setStatus(0);
        int i = mapper.insertLoginTicket(loginTicket);
        System.out.println(loginTicket + " " + i);
    }

    @Test
    public void Select(){
        String s = new String("2a8adfb6af654982ad615a88d683183c");
        LoginTicket loginTicket = mapper.selectByTicket(s);
        System.out.println(loginTicket);
        int i = mapper.updateStatus(s, 1);
        loginTicket = mapper.selectByTicket(s);
        System.out.println(loginTicket);
    }
}
