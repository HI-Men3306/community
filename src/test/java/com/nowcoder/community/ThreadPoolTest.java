package com.nowcoder.community;

import com.nowcoder.community.controller.interceptor.AlphaInterceptor;
import com.nowcoder.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTest {
    @Autowired
    private AlphaService service;

    private void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestOfScheduled(){
        for (int i = 0; i < 10; i++) {
            service.Async();
        }
        sleep(1000000);
    }

    @Test
    public void SyncTest(){
        sleep(30000);
    }

}
