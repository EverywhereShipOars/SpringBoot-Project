package com.lyl.news.interceptor;

import com.lyl.news.dao.LoginTicketDAO;
import com.lyl.news.dao.UserDAO;
import com.lyl.news.models.HostHolder;
import com.lyl.news.models.LoginTicket;
import com.lyl.news.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    private UserDAO userDAO;

    @Override
    //在访问页面前，验证是否是登陆用户，如果是，则把用户保存到hostHolder中，这就是识别用户的逻辑
    //啥意思？相当于我每次点开一个url，也就是说每次访问Controller之前，都会经历这一步
    //会验证客户端的Cookie中是否有ticket字段，且这个ticket是否有效，如果有效，则认定它为某登陆用户
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = null;
        //查ticket是否存在
        if(request.getCookies() != null){
            for (Cookie cookie : request.getCookies()){
                if (cookie.getName().equals("ticket")){
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
        //查ticket是否有效
        if(ticket != null){
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
            if(loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0){
                return true;
            }
            //ticket有效，则将它保存到hostHolder中，之后后台如果用得到这个信息，则可以使用hostHolder来获取user
            User user = userDAO.selectById(loginTicket.getUserId());
            hostHolder.setUser(user);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if(modelAndView != null && hostHolder.getUser() != null){
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
