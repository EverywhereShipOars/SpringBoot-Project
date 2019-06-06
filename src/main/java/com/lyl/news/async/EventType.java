package com.lyl.news.async;

/**
 *  EventType的作用是: 确定待处理的这个异步事件是什么类型的事件,
 *  方便最后消费者取出来后根据它的类型来调用相应的Handler
 */
public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2);

    private int value;

    EventType(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }
}
