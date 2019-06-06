package com.lyl.news.async.handler;

import com.lyl.news.async.EventHandler;
import com.lyl.news.async.EventModel;
import com.lyl.news.async.EventType;
import com.lyl.news.models.Message;
import com.lyl.news.models.User;
import com.lyl.news.service.MessageService;
import com.lyl.news.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
//点赞后，系统会给用户发一封站内信
public class LikeHandler implements EventHandler {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    public void doHandler(EventModel model) {
        Message message = new Message();
        User user = userService.getUser(model.getActorId());
        int toId = model.getEntityOwner();
        message.setToId(toId);
        message.setCreatedDate(new Date());
        //系统用户
        message.setFromId(3);
        message.setContent("用户" + user.getName() +
                "赞了你的资讯http://localhost:8080/news" + String.valueOf(model.getEntityId()));
        message.setConversationId(toId < 3 ? String.format("%d_%d", toId, 3) : String.format("%d_%d", 3, toId));
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
