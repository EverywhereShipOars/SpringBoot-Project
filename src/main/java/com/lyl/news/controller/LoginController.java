package com.lyl.news.controller;

import com.lyl.news.models.HostHolder;
import com.lyl.news.service.UserService;
import com.lyl.news.util.NewsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = "/reg", method = {RequestMethod.POST})
    @ResponseBody
    public String reg(@RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "rember", defaultValue = "0") int rememberme,
                      HttpServletResponse response){
        try {
            Map<String, Object> map = userService.register(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                //如果勾选了记住我，则cookie的值被存为一礼拜
                if (rememberme > 0){
                    cookie.setMaxAge(3600 * 24 * 7);
                }
                response.addCookie(cookie);
                return NewsUtil.getJSONString(0, "注册成功，已登陆");
            } else {
                return NewsUtil.getJSONString(1, map);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return NewsUtil.getJSONString(1, "注册异常: " + e.getMessage());
        }
    }

    @RequestMapping(value = "login", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rember", defaultValue = "0") int rememberme,
                        HttpServletResponse response){
        try {
            Map<String, Object> map = userService.login(username, password);
            if (map.containsKey("ticket")) {
                //将ticket下发给cookie
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme > 0){
                    cookie.setMaxAge(3600 * 24 * 7);
                }
                response.addCookie(cookie);
                return NewsUtil.getJSONString(0, "登陆成功");
            } else {
                return NewsUtil.getJSONString(1, "登陆失败");
            }
        }catch (Exception e){
            return NewsUtil.getJSONString(1, "登陆异常: " + e.getMessage());
        }
    }

    //写来测试检验服务端是否能辨认用户的
    @RequestMapping("/login/test")
    @ResponseBody
    public String test(){
        if(hostHolder.getUser() != null){
            return NewsUtil.getJSONString(0, "登陆的用户为: " + hostHolder.getUser().getName());
        }else{
            return NewsUtil.getJSONString(1, "用户未登录");
        }
    }
}
