package com.lyl.news.controller;

import com.lyl.news.models.HostHolder;
import com.lyl.news.service.UserService;
import com.lyl.news.util.NewsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;


    @RequestMapping(value = "/reg", method = {RequestMethod.POST})
    public String reg(@RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "rember", defaultValue = "0") int rememberme,
                      HttpServletResponse response,
                      Model model){
        try{
            Map<String, String> map = userService.register(username, password);
            if (map.containsKey("success")){
                //向模版注入注册成功信息, 并跳转到登陆页，我这里不使用叶神课上的注册成功后就登陆这个逻辑，而是分开
                model.addAttribute("success", "注册成功");
                return "redirect:/login";
            }else{
                model.addAttribute("error", "注册失败");
                return "redirect:/register";
            }

        }catch (Exception e){
            logger.error("error", "注册异常");
            return "redirect:/register";
        }

    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rember", defaultValue = "0") int rememberme,
                        HttpServletResponse response,
                        Model model){
        try {
            Map<String, String> map = userService.login(username, password);
            if (map.containsKey("ticket")){
                //将这个ticket存入cookie, 设置cookie的范围和有效期，并随着response返回给客户端
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                if (rememberme > 0){
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);
                return "redirect:/";
            }else{
                model.addAttribute("error", "登陆失败");
                return "redirect:/login/";
            }
        }catch (Exception e){
            logger.error("error", "登陆失败");
            return "redirect:/login/";
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
