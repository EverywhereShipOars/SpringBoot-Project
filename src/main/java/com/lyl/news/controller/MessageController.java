package com.lyl.news.controller;

import com.lyl.news.dao.MessageDAO;
import com.lyl.news.models.HostHolder;
import com.lyl.news.models.Message;
import com.lyl.news.models.User;
import com.lyl.news.models.ViewObject;
import com.lyl.news.service.MessageService;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    //站内信不管在详情页还是在主页的显示，都是通过读取message表来实现的。
    @RequestMapping(value = "/msg/addMessage", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content){
        Message message = new Message();

        fromId = hostHolder.getUser().getId();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setContent(content);
        message.setCreatedDate(new Date());
        //由于会话是由聊天的双方共同维护的，因此两个人的聊天应该被设置成一个会话
        //这里规定会话的值为: "smallId_bigId"。
        message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId)
                                  : String.format("%d_%d", toId, fromId));
        messageService.addMessage(message);

        return NewsUtil.getJSONString(message.getId());
    }

    @RequestMapping(value = "/msg/detail", method = RequestMethod.GET)
    public String conversationDetail(Model model, @RequestParam("conversationId") String conversationId) {
        try{
            List<ViewObject> messages = new ArrayList<>();
            //按会话id来查询俩人的聊天详情数据，根据id来排序，id小的自然排在前面
            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);
            for (Message msg : conversationList){
                ViewObject vo = new ViewObject();
                //向模版中注入message的信息
                vo.set("message", msg);
                //通过fromId来找出发信人，注入模版，因为模版要显示双方头像姓名啥的
                User user = userService.getUser(msg.getFromId());
                if (user == null){
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userName", user.getName());
                vo.set("user", user);
                messages.add(vo);
            }
            model.addAttribute("messages", messages);
            return "letterDetail";
        }catch (Exception e){
            logger.error("获取聊天详情页失败 " + e.getMessage());
            return "redirect:/letter";
        }
    }

    @RequestMapping(value = "/msg/list", method = RequestMethod.GET)
    public String conversationList(Model model){
        try {
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<>();
            List<Message> messages = messageService.getConversationList(localUserId, 0, 10);
            for (Message msg : messages) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                //和对方聊天，显示的自然是对方的信息
                int targetId = msg.getToId() == localUserId ? msg.getFromId() : msg.getToId();
                User user = userService.getUser(targetId);
                vo.set("user", user);
                vo.set("conversationsCount", messageService.getConversationCout(msg.getConversationId()));
                vo.set("unread", messageService.getUnreadCount(localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute(conversations);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return "letter";
    }




}
