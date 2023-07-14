package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

//发送邮件工具
@Component
public class MailClient {
    //创建自定义日志
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    //JavaMailSender是Spring框架提供的一个接口，用于发送电子邮件
    @Autowired
    private JavaMailSender mailSender;
    //因为要发送的邮箱的发送人是固定的 所有直接获取配置文件中配置好的邮箱也就是发送邮箱
    //@Value注解不仅可以注入简单类型  也可以注入配置文件中的属性值
    @Value("${spring.mail.username}")
    private String From;

    public void sendMail(String to,String subject,String content){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);//创建辅助类简化对MimeMessage的操作 构建message
            helper.setFrom(From);//设置发送邮箱
            helper.setTo(to);//设置接收邮箱
            helper.setSubject(subject);//设置邮件主题
            helper.setText(content,true);//设置邮件内容 允许发送html格式的字符串
            mailSender.send(helper.getMimeMessage());//发送邮件
        } catch (MessagingException e) {
            logger.error("发送邮件失败" + e.getMessage());
        }
    }
}
