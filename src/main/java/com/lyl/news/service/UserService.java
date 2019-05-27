package com.lyl.news.service;

import com.lyl.news.dao.LoginTicketDAO;
import com.lyl.news.dao.UserDAO;
import com.lyl.news.models.LoginTicket;
import com.lyl.news.models.News;
import com.lyl.news.models.User;
import com.lyl.news.util.NewsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public Map<String, Object> register(String username, String password){
        Map<String, Object> map = new HashMap<>();
        //对用户名和密码进行判断
        if (username == null || username.length() == 0){
            map.put("messagename", "用户名不能为空");
            return map;
        }
        if (username.length() < 3){
            map.put("messagename", "用户名长度不能小于3");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user != null){
            map.put("messagename", "用户名已存在");
            return map;
        }
        if (password == null || password.length() == 0){
            map.put("messagepassword", "密码不能为空");
            return map;
        }
        if(password.length() < 3){
            map.put("messagepassword", "密码长度不能少于3位");
            return map;
        }
        //用户名无误后，向User表中插入一条数据
        user = new User();
        user.setName(username);
        String salt = UUID.randomUUID().toString().substring(0, 5);
        user.setPassword(NewsUtil.MD5(password + salt));
        user.setSalt(salt);
        String head = String.format("/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        userDAO.addUser(user);

        //课程里的逻辑是，注册后直接登陆了
        String ticket = addTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }

    public Map<String, Object> login(String username, String password){
        Map<String, Object> map = new HashMap<>();
        //进行一系列登陆的逻辑判断
        if(username == null || username.length() < 1){
            map.put("messagename", "用户名不能为空");
            return map;
        }
        if(password == null || password.length() < 1){
            map.put("messagepassword", "密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user == null){
            map.put("messagename", "用户名不存在");
            return map;
        }
        if(!user.getPassword().equals(NewsUtil.MD5(password + user.getSalt()))){
            map.put("messagepassword", "密码错误");
            return map;
        }
        //用户名和密码无误后，给登陆的用户发一个ticket
        String ticket = addTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }

    //其实就是将ticket和user绑定起来
    private String addTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        //生成一串随机的ticket
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicket.setUserId(userId);
        //设置ticket的有效期
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 3600 * 24);
        loginTicket.setExpired(date);
        //设置状态为0，表示有效
        loginTicket.setStatus(0);
        loginTicketDAO.addLoginTicket(loginTicket);

        //返回ticket码给service, 再由controller层写到cookie中发给客户端
        return loginTicket.getTicket();
    }

    public User getUser(int id){
        return userDAO.selectById(id);
    }

}
