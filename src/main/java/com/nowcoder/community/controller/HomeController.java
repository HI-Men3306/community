package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService  likeService;


    @RequestMapping(path = "/index", method = RequestMethod.GET)
    //orderMode是标志以什么样的方式来展现帖子列表  时间排列0 还是 热度排列1   默认为0
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode) {
        System.out.println(orderMode);
        // 方法调用前,SpringMVC会自动实例化Model和Page,并将Page注入Model.
        // 所以,在thymeleaf中可以直接访问Page对象中的数据.
        page.setRows(discussPostService.SelectCount(0));
        //分页信息的地址  干什么用的？  当点击分页之后 会发送一个请求，这里的path就是发送请求的地址
        //如何实现了当点击不同的页码时，跳转页面？
        //当点击页码之后，会重新发送一个请求，而请求的地址就是page中的path，
        //而且会在地址的后面拼接上current参数并自动封装进page中表明要跳转到第几页
        //后面检索的数据会根据当前页数 查询对应范围内的数据
        page.setPath("/index?orderMode=" + orderMode);

        //这里根据返回的page中的current当前页数  计算出要查询数据的范围
        List<DiscussPost> list = discussPostService.SelectDiscussPost(0, page.getOffset(), page.getLimit(),orderMode);
        //因为要展示给前端视图 需要user 和 discussPost两个信息 所以要将其对应起来
        //所以使用list嵌套map  每一个map中存放当前帖子的信息和发帖人的信息
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.SelectUserById(post.getUserId());
                map.put("user", user);

                //查询对应帖子的点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute(page);
        model.addAttribute("orderMode",orderMode);
        return "/index";
    }


    /*
    * 测试用的
    * */
    @RequestMapping(path = "/web",method = RequestMethod.GET)
    public String test(Model model, Page page){
        System.out.println("进入");
        List<Map<String,Object>> NewList = new ArrayList<>();
        Map<String,Object> mapOne = new HashMap<>();
        Map<String,Object> mapTwo = new HashMap<>();
        mapOne.put("user",userService.SelectUserById(101));
        mapOne.put("post",discussPostService.SelectById(109));
        mapTwo.put("user",userService.SelectUserById(102));
        mapTwo.put("post",discussPostService.SelectById(110));
        NewList.add(mapOne);
        NewList.add(mapTwo);
        model.addAttribute("NewList",NewList);


        page.setRows(discussPostService.SelectCount(0));
        //设置地址   也就是之后的翻页要给谁发送请求
        page.setPath("/web");

        System.out.println("进入index");

        List<DiscussPost> list = discussPostService.SelectDiscussPost(0, page.getOffset(), page.getLimit(),0);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.SelectUserById(post.getUserId());
                System.out.println(post);
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPost", discussPosts);
        return "index2";
    }

    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }

    //拒绝访问时的提示页面（越级访问）
    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDeniedPage() {
        //即使没有404相对应的图片或网站   springmvc会默认给出错误信息提示页面
        return "/error/404";
    }
}
