package com.lyl.news.controller;

import com.lyl.news.models.*;
import com.lyl.news.service.LikeService;
import com.lyl.news.service.NewsService;
import com.lyl.news.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;


    //首页显示的应该是最近的一些新闻
    private List<ViewResult> getNewsDto(int offset, int limit){
        List<ViewResult> vos = new ArrayList<>();
        List<News> newsList = newsService.getLatestNews(offset, limit);
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        for (News news : newsList){
            ViewResult vo = new ViewResult();
            vo.setNews(news);
            //这里注入的是发资讯的用户
            vo.setUser(userService.getUser(news.getUserId()));
            //这里注入的是当前的点赞状态，而非点赞数量，点赞数量应该在news中体现。未登录则没有点赞情况
            if (localUserId != 0){
                vo.setLike(likeService.getLikeStatus(localUserId, news.getId(), EntityType.ENTITY_NEWS));
            }else{
                vo.setLike(0);
            }
            vos.add(vo);
        }
        return vos;
    }


    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String index(Model model){
        //显示最近的20条资讯的相关信息
        model.addAttribute("vos", getNewsDto(0, 20));
        // TODO: 为什么要添加user? pop又是个什么?
        model.addAttribute("user", hostHolder.getUser());

        return "home";
    }



}
