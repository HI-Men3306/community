package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.Thymeleaf;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TemplateEngine engine;//创建Thymeleaf模板引擎 用于发送HTML格式的邮件信息

    @Autowired
    private MailClient mailClient;

    @Value("${server.servlet.context-path}")
    private String contextPath;//上下文路径

    @Value("${community.path.domain}")
    private String domain;//域名地址

    public User SelectUserById(int id) {
        return userMapper.selectById(id);
    }

    //用户注册
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        //先对user及其属性进行判断
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }

        //查询数据库 验证账号
        User res = userMapper.selectByName(user.getUsername());
        if (res != null) {
            map.put("usernameMsg", "用户已存在！");
            return map;
        }
        //查询数据库 验证邮箱是否存在
        res = userMapper.selectByEmail(user.getEmail());
        if (res != null) {
            map.put("emailMsg", "该邮箱已被注册！");
            return map;
        }
        //用户注册
        //先对用户信息进行初始化设置

        //随机生成密码后面的后缀字符串 截取五位
        user.setSalt(CommunityUtil.GenerateUUID().substring(0, 5));
        //根据用户的密码和随机生成的字符串进行MD5加密
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        //设置用户激活码
        user.setActivationCode(CommunityUtil.GenerateUUID());
        //设置用户初始化头像
        //使用了字符串格式化方法 String.format() 来将占位符 %dt 替换为一个随机整数(范围在0到999之间),然后拼接成一个完整的图片URL地址
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        //设置用户创建时间
        user.setCreateTime(new Date());
        //设置用户状态
        user.setStatus(0);
        //设置用户类型
        user.setType(0);
        //创建用户
        userMapper.insertUser(user);

        //设置激活页面
        Context context = new Context();
        //向前端页面传输 要显示的用户名
        context.setVariable("email", user.getEmail());
        //传输 要点击激活时连接时 要发送请求给activation这个请求路径
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        //将context域作为参数 传递给"/mail/activation"视图模板 生成HTML格式的字符串
        //利用Thymeleaf模板生成的发送邮件中的HTML内容  以便在生成HTML电子邮件时使用上下文中的变量和属性。
        String content = engine.process("/mail/activation", context);
        //发送激活邮件        将生成的HTML格式的邮件内容通过mailClient发送给指定邮箱
        mailClient.sendMail(user.getEmail(),"激活验证",content);

        return map;
    }
}
