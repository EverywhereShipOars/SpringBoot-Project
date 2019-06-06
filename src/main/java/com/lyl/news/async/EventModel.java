package com.lyl.news.async;

import com.lyl.news.models.EntityType;

import java.util.HashMap;
import java.util.Map;

/**
 *  异步处理事件的model。
 */
public class EventModel {

    //事件是什么类型的，这个决定之后的消费者调用哪个handler来处理这个事件
    private EventType type;
    //事件的发起者，例如在点赞后发送站内信通知被赞者这个业务场景，A给B点赞，A就是发起者。
    private int actorId;
    //这个操作所关联的类型和id，例如给id为6的资讯点赞，entityType=EntityType.NEWS，entityId=6，这里的entityOwner就是B
    private int entityId;
    private int entityType;
    private int entityOwner;
    //这个exts是用来存一些这个异步事件处理需要的额外的数据
    private Map<String, String> exts = new HashMap<>();

    public EventModel() {
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public void setExts(Map<String, String> exts) {
        this.exts = exts;
    }

    public EventModel(EventType type){
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public int getActorId() {
        return actorId;
    }

    public void setActorId(int actorId) {
        this.actorId = actorId;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityOwner() {
        return entityOwner;
    }

    public void setEntityOwner(int entityOwner) {
        this.entityOwner = entityOwner;
    }
}
