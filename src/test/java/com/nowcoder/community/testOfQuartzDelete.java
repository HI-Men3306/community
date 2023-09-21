package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class testOfQuartzDelete {
    @Autowired
    private Scheduler scheduler;
    @Test
    public void DeleteQuartz() throws SchedulerException {
        //从数据库中删除定时任务
        scheduler.deleteJob(new JobKey("alphaJob","alphaJobGroup"));
    }
}
