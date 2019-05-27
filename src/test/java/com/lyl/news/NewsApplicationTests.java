package com.lyl.news;

import com.lyl.news.dao.*;
import com.lyl.news.models.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NewsApplicationTests {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private NewsDAO newsDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private MessageDAO messageDAO;

    @Test
    public void contextLoads() {
        Message message = new Message();

        message.setToId(2);
        message.setFromId(5);
        message.setConversationId("2_5");
        message.setCreatedDate(new Date());
        message.setContent("hello, lyl");

        System.out.println(messageDAO.addMessage(message));
    }

}
