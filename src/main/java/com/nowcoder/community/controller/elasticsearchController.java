package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class elasticsearchController implements CommunityConstant{
    @Autowired
    private UserService userService;

    @Autowired
    private ElasticsearchService searchService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public String searchDiscussPost(String keyword, Page page, Model model){
        //搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult =
                searchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());

        //聚合数据信息
        List<Map<String,Object>> dataList = new ArrayList<>();
        //查询不到该关键字  直接提示错误
        if(searchResult == null){
            model.addAttribute("error","啊偶，没有找到该信息哦！");
            return "/site/search";
        }
        for (DiscussPost discussPost : searchResult) {
            HashMap<String, Object> map = new HashMap<>();

            //存放帖子
            map.put("discuss",discussPost);

            //帖子作者
            User user = userService.SelectUserById(discussPost.getUserId());
            map.put("user",user);

            //帖子的点赞数量
            map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPost.getId()));

            dataList.add(map);
        }
        model.addAttribute("dataList",dataList);
        //传输关键字信息
        model.addAttribute("keyword",keyword);

        //设置分页信息
        //设置总的信息数量为查询到的符合关键字的帖子数量
        page.setRows(searchResult == null ? 0 : (int)searchResult.getTotalElements());
        page.setPath("/search?keyword=" + keyword);

        return "/site/search";
    }
}
