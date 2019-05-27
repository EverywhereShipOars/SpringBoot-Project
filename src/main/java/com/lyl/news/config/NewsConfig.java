package com.lyl.news.config;

import com.lyl.news.interceptor.LoginRequiredInterceptor;
import com.lyl.news.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class NewsConfig implements WebMvcConfigurer {

    @Autowired
    private PassportInterceptor passportInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        //注意需要按顺序
        //第一个拦截器做的通过cookie来判断用户的登陆状态，而第二个拦截器的作用是拦截未登陆的用户
        registry.addInterceptor(passportInterceptor).addPathPatterns("/**");
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/msg/**");
    }

}
