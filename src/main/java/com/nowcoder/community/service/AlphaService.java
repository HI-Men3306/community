package com.nowcoder.community.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AlphaService {
    private static Logger logger = LoggerFactory.getLogger(AlphaService.class);

    //@Async
    public void Async() {
        logger.debug("异步");
    }

    //@Scheduled(initialDelay = 10000, fixedRate = 1000)
    public void execute2() {
        logger.debug("普通定时任务");
    }
}
