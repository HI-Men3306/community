package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class kaptchaConfig {
    //Bean的作用是将一个方法的返回值纳入spring容器管理
    @Bean
    public Producer kaptchaProducer(){
        //配置kaptcha配置信息  即生成的随机验证码的图片大小 字体大小 等
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");//生成范围
        properties.setProperty("kaptcha.textproducer.char.length", "4");//生成的随机字符串长度
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");//不使用额外干扰

        Config config = new Config(properties);//生成一个配置文件的配置信息
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(config);//根据配置 生成一个kaptcha对象
        return kaptcha;
    }
}
