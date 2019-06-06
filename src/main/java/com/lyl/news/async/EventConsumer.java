package com.lyl.news.async;

import com.alibaba.fastjson.JSONObject;
import com.lyl.news.util.JedisAdapter;
import com.lyl.news.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  消费者。
 *  消费者做的事情就是首先将每个eventType对应的Handler给注册好
 *  然后新开启一个线程，当队列不为空的时候，就从队列中取出元素，并反序列化成EventModel
 *  然后根据它的eventType，去调用相应的Handler处理
 *
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    //config是用来存每个类型的事件对应哪些Handler
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception{
        //获取所有Handler的具体的对象
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        //注册Handler，将每个type对应的Handler给存好
        for (Map.Entry<String, EventHandler> entry : beans.entrySet()){
            EventHandler handler = entry.getValue();
            for (EventType eventType : handler.getSupportEventTypes()){
                if (config.get(eventType) == null){
                    config.put(eventType, new ArrayList<EventHandler>());
                }
                config.get(eventType).add(handler);
            }
        }

        //启动线程去消费事件
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> messages = jedisAdapter.brpop(3, key);
                    for (String message : messages){
                        if (message.equals(key)){
                            continue;
                        }
                        //将队列中的事件取出来后并反序列化成EventModel对象
                        EventModel eventModel = JSONObject.parseObject(message, EventModel.class);
                        if (!config.containsKey(eventModel.getType())){
                            logger.error("不能识别的事件" + eventModel.getType());
                            continue;
                        }
                        //之前已经将每个eventType所对应的Handler注册到config里去了
                        for (EventHandler handler : config.get(eventModel.getType())){
                            handler.doHandler(eventModel);
                        }

                    }
                }
            }
        });

        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
