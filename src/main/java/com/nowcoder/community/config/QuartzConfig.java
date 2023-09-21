package com.nowcoder.community.config;

import com.nowcoder.community.quartz.AlphaJob;
import com.nowcoder.community.quartz.PostReferenceJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

//配置只有在程序第一次启动时才会使用，直接将配置的信息初始化到数据库中 以后的程序执行都访问数据库来调度任务job
// 配置 -> 数据库 -> 调用
@Configuration
public class QuartzConfig {
    // FactoryBean可简化Bean的实例化过程:
    // 1.通过FactoryBean封装Bean的实例化过程.
    // 2.将FactoryBean装配到Spring容器里.
    // 3.将FactoryBean注入给其他的Bean.
    // 4.该Bean得到的是FactoryBean所管理的对象实例.

    // 配置JobDetail
    // Scheduler调度执行一个Job的时候，首先会拿到对应的Job，然后创建该Job实例，再去执行Job中的execute()方法。
    // JobDetail对象包含了Job的所有信息，如名称、组名、作业类别、作业状态、触发器等。
    //@Bean
    public JobDetailFactoryBean alphaJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();//创建factoryBean
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }


    // 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
    // 触发器
    //@Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);//触发job的时间间隔
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }


    @Bean
    public JobDetailFactoryBean postReferenceJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();//创建factoryBean
        factoryBean.setJobClass(PostReferenceJob.class);
        factoryBean.setName("postReferenceRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }


    // 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
    // 触发器
    @Bean
    public SimpleTriggerFactoryBean postReferenceTrigger(JobDetail postReferenceJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postReferenceJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 5);//五分钟触发一次
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
