package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling//简化spring定时线程池 书写格式
@EnableAsync//Spring容器将开启对异步任务的支持，并自动配置一个线程池
//添加@Async注解的方法 将在程序开始时 由spring自动进行异步调用
public class ThreadPoolConfig {
}
