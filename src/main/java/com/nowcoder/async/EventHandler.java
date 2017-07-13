package com.nowcoder.async;

import java.util.List;

/**
 * Created by LIU ZHAOZHI on 2017-7-7.
 */
public interface EventHandler {
    void doHandler(EventModel model);

    List<EventType> getSupportEventTypes(); //注册自己，说明自己是关注哪种event
}
