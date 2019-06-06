package com.lyl.news.async;

import com.alibaba.fastjson.JSONObject;
import com.lyl.news.util.JedisAdapter;
import com.lyl.news.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 *  生产者
 *  生产者干的事情比较简单，就是把需要异步处理的事件给序列化了，然后丢进异步队列。这里的队列使用Redis中的list来实现
 */
@Service
public class EventProducer {

    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);

    @Autowired
    private JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel){
        try{
            //将Java对象序列化成Json格式
            String json = JSONObject.toJSONString(eventModel);
            //获得在Redis中用于存储异步事件的list的key
            String key = RedisKeyUtil.getEventQueueKey();
            logger.info("产生一个异步事件" + eventModel.getType());
            jedisAdapter.lpush(key, json);
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
