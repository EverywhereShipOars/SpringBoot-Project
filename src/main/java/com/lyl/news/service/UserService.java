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

    public Map<String, String > register(String username, String password){
        Map<String, String> map = new HashMap<>();
        if (username == null || username.length() < 4){
            map.put("usermsg", "用户名为空或用户名不合法");
            return map;
        }
        if (password == null || password.length() < 4){
            map.put("passwdmsg", "密码为空或密码不合法");
            return map;
        }
        if (userDAO.selectByName(username) != null){
            map.put("usermsg", "用户名已存在");
            return map;
        }

        //账号和密码都已满足要求，开始新增一个用户的逻辑
        User user = new User();
        user.setName(username);
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        //通过加salt和md5加密的方式增加密码强度
        String salt = UUID.randomUUID().toString().substring(0, 5);
        user.setSalt(salt);
        user.setPassword(NewsUtil.MD5(password + salt));
        userDAO.addUser(user);

        //在map中返回一个注册成功的信息, 方便controller层判断是否注册成功，并执行相应的逻辑
        map.put("success", "success");
        return map;

    }

    public Map<String, String> login(String username, String password){
        Map<String, String> map = new HashMap<>();
        //用户名和密码合法性判别
        if (username == null || username.length() < 4){
            map.put("usermsg", "用户名为空或用户名不合法");
            return map;
        }
        if (password == null || password.length() < 4){
            map.put("passwdmsg", "密码为空或密码不合法");
            return map;
        }
        User user = userDAO.selectByName(username);
        if (user == null){
            map.put("usermsg", "用户名或密码错误");
            return map;
        }
        //生成一个ticket并和用户绑定
        map.put("ticket", addTicket(user.getId()));
        return map;
    }

    //将ticket和user绑定起来，并设置好过期时间，状态等一系列信息，最后返回一个凭证号
    private String addTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();

        loginTicket.setStatus(0);
        Date date = new Date();
        date.setTime(date.getTime() + 3600 * 24 * 1000);
        loginTicket.setExpired(date);
        loginTicket.setUserId(userId);
        String ticket = UUID.randomUUID().toString().replaceAll("-", "");
        loginTicket.setTicket(ticket);

        return ticket;
    }

    public User getUser(int id){
        return userDAO.selectById(id);
    }

}
