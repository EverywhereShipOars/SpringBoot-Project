package com.lyl.news.controller;

import com.lyl.news.async.EventModel;
import com.lyl.news.async.EventProducer;
import com.lyl.news.async.EventType;
import com.lyl.news.models.EntityType;
import com.lyl.news.models.HostHolder;
import com.lyl.news.models.News;
import com.lyl.news.service.LikeService;
import com.lyl.news.service.NewsService;
import com.lyl.news.util.NewsUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(value = "/like", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@Param("newsId") int newsId){
        int userId = hostHolder.getUser().getId();
        long likeCount = likeService.like(userId, newsId, EntityType.ENTITY_NEWS);
        newsService.updateLikeCount(newsId, (int)likeCount);
        News news = newsService.getById(newsId);
        //点赞后，会产生一个异步事件，主线程需要做的其实就是把这个异步事件丢入队列中，就ok了。
        //至于这个事件啥时候被处理，如何被处理，都和主线程无关了，而是另外开一个线程去处理。因此是异步的
        EventModel model = new EventModel(EventType.LIKE);
        model.setActorId(hostHolder.getUser().getId());
        model.setEntityType(EntityType.ENTITY_NEWS);
        model.setEntityId(news.getId());
        model.setEntityOwner(news.getUserId());
        eventProducer.fireEvent(model);
        return NewsUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(value = "/dislike", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dislike(@Param("newsId") int newsId){
        int userId = hostHolder.getUser().getId();
        long likeCount = likeService.dislike(userId, newsId, EntityType.ENTITY_NEWS);
        newsService.updateLikeCount(newsId, (int)likeCount);
        return NewsUtil.getJSONString(0, String.valueOf(likeCount));
    }


}
