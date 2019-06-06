package com.lyl.news.async;

import java.util.List;

/**
 *  事件处理的统一接口
 */
public interface EventHandler {
    void doHandler(EventModel model);

    List<EventType> getSupportEventTypes();
}
