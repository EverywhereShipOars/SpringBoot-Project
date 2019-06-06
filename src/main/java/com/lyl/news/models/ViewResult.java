package com.lyl.news.models;

/**
 *  为了方便把资讯整条整条的注入(一条资讯包含赞踩、新闻、user三个部分)
 *  这里引入一个ViewResult类
 */
public class ViewResult {

    private User user;
    private News news;
    private int like;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }
}
