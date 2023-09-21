package com.nowcoder.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
public class WkConfig {
    private static final Logger logger = LoggerFactory.getLogger(WkConfig.class);

    @Value("${wk.image.storage}")
    private String WkImageStorage;

    //在程序启动时 创建保存长图的文件夹
    @PostConstruct
    public void ini(){
        File file = new File(WkImageStorage);
        if(!file.exists()){
            file.mkdir();
            logger.info("创建保存长图文件!");
        }
    }
}
