package com.lyl.news.controller;

import com.lyl.news.models.Message;
import com.lyl.news.service.MessageService;
import com.lyl.news.util.NewsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;


    @RequestMapping(value = "/addMessage", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content){
        Message message = new Message();
        message.setContent(content);
        message.setFromId(fromId);
        message.setToId(toId);
        message.setCreatedDate(new Date());
        //这里把会话的id设置为从发送接收方的较小者发给较大者这样一个格式
        message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
        messageService.addMessage(message);

        return NewsUtil.getJSONString( message.getId());
    }


}
