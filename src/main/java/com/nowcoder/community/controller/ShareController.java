package com.nowcoder.community.controller;

import com.nowcoder.community.Event.EventProducer;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ShareController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Autowired
    private EventProducer eventProducer;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    //存放分享图的库url地址
    @Value("${qiniu.bucket.share.url}")
    private String shareBucketUrl;

    @RequestMapping(path = "/share",method = RequestMethod.GET)
    @ResponseBody
    public String share(String htmlURL){
        //异步生成长图  所以要使用kafka来消费事件

        //生成文件名
        String imageName = CommunityUtil.GenerateUUID();

        //发送异步事件
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl", htmlURL)//要生成长图的网页地址
                .setData("fileName", imageName)//生成的图片名称
                .setData("suffix", ".png");//生成图片的后缀
        //发送事件
        eventProducer.sendEvent(event);

        //将 访问长图的路径 以json格式 响应到页面上
        Map<String,Object> map = new HashMap<>();
        //map.put("shareUrl",domain + contextPath + "/share/image/" + imageName);
        //替换为七牛云的访问路径
        map.put("shareUrl",shareBucketUrl + "/" + imageName);

        return CommunityUtil.getJSONString(0, null, map);
    }




    //获取长图
    @RequestMapping(path = "/share/image/{imageName}",method = RequestMethod.GET)
    @ResponseBody
    public void getShareImage(@PathVariable("imageName")String imageName, HttpServletResponse response){
        if(StringUtils.isBlank(imageName)){
            throw new IllegalArgumentException("文件名不能为空!");
        }

        //设置响应类型
        response.setContentType("image/png");
        //获取图片
        File file = new File(wkImageStorage + "/" + imageName + ".png");

        //响应
        try {
            OutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("获取长图失败: " + e.getMessage());
        }
    }



    /*@RequestMapping(path = "/share",method = RequestMethod.GET)
    public String share(String htmlURL,HttpServletRequest request){
        //异步生成长图  所以要使用kafka来消费事件

        System.out.println("传入的网站为" + htmlURL);

        //生成文件名
        String imageName = CommunityUtil.GenerateUUID();

        //发送异步事件
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl", htmlURL)//要生成长图的网页地址
                .setData("fileName", imageName)//生成的图片名称
                .setData("suffix", ".png");//生成图片的后缀
        //发送事件
        eventProducer.sendEvent(event);

        System.out.println("发送事件成功");


        //将 访问长图的路径 以json格式 响应到页面上
        Map<String,Object> map = new HashMap<>();
        String imageUrl = domain + contextPath + "/share/image/" + imageName;

        request.setAttribute("imageName",imageName);

        return "redirect:/share/image";
    }

    //获取长图
    @RequestMapping(path = "/share/image",method = RequestMethod.GET)
    @ResponseBody
    public void getShareImage(HttpServletResponse response, HttpServletRequest request){
        String imageName = (String) request.getAttribute("imageName");
        if(StringUtils.isBlank(imageName)){
            throw new IllegalArgumentException("文件名不能为空!");
        }

        System.out.println("详细路径为" + request.getRequestURI());
        System.out.println("文件名为" + imageName);

        //设置响应类型
        response.setContentType("image/png");
        //获取图片
        File file = new File(wkImageStorage + "/" + imageName + ".png");

        //响应
        try {
            OutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("获取长图失败: " + e.getMessage());
        }
    }*/
}
