package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {
    @Autowired
    private MailClient mailClient;

    //启动Thymeleaf用
    @Autowired
    private TemplateEngine engine;

    @Test
    public void TestOfSendMail(){
        mailClient.sendMail("2975201859@qq.com","你好啊","welcome to my mailSend of auto");
        System.out.println("发送成功");
    }

    @Test
    public void TestOfHTMLMail(){
        Context context = new Context();
        //设置向前端视图共享的数据
        context.setVariable("username","小黄狗");
        /*
        * 这段代码使用名为 engine 的邮件引擎处理一个名为 "mail/demo" 的模板，并将上下文对象 context 作为参数传递给该模板。
        在这种情况下，engine 是一个用于生成 HTML 电子邮件的引擎，它可以读取和渲染模板文件，并将其转换为 HTML 格式的字符串。
        * 通过将模板名称和上下文对象作为参数传递给 process() 方法，可以将模板与上下文数据结合在一起，
        * 以便在生成 HTML 电子邮件时使用上下文中的变量和属性。
        * */
        String html = engine.process("mail/demo", context);
        mailClient.sendMail("2232324044@qq.com","你好",html);
        System.out.println("发送成功");
    }
}
