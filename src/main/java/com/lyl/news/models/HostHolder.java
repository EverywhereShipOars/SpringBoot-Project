package com.lyl.news.models;

import org.springframework.stereotype.Component;

/**
 *  二刷的时候需要找点资料彻底理解一下这个类啊，不太懂ThreadLocal这个类
 *  暂时理解成这个玩意可能单独开一个线程，用来保存User这个变量即可
 */

@Component
public class HostHolder {

    private static ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);
    }

    public void clear(){
        users.remove();
    }

}
