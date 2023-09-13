package com.nowcoder.community.controller;

import com.nowcoder.community.service.TotalDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class TotalDataController {
    @Autowired
    private TotalDataService service;

    @RequestMapping(path = "/data",method = {RequestMethod.POST,RequestMethod.GET})
    public String getDataPage(){
        return "/site/admin/data";
    }

    //获取UV 访问量
    @RequestMapping(path = "/data/UV",method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd")Date end, Model model){
        long res = service.calculateUV(start, end);
        model.addAttribute("uvResult",res);
        //用户数据的回显
        model.addAttribute("uvStart",start);
        model.addAttribute("uvEnd",end);
        return "forward:/data";
    }

    //获取DAU 访问量
    @RequestMapping(path = "/data/DAU",method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd")Date end, Model model){
        long res = service.calculateDAU(start, end);
        model.addAttribute("dauResult",res);
        //用户数据的回显
        model.addAttribute("dauStart",start);
        model.addAttribute("dauEnd",end);
        return "forward:/data";
    }
}
