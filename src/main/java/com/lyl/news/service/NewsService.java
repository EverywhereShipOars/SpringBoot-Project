package com.lyl.news.service;

import com.lyl.news.dao.NewsDAO;
import com.lyl.news.models.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {

    @Autowired
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int offset, int limit){
        return newsDAO.selectByUserIdAndOffset(offset, limit);
    }

    public News getById(int newsId){
        return newsDAO.selectById(newsId);
    }

    public void updateCommentCount(int newsId, int count){
        newsDAO.updateCommentCount(newsId, count);
    }

    public void updateLikeCount(int newsId, int count){
        newsDAO.updateLikeCount(newsId, count);
    }

}
