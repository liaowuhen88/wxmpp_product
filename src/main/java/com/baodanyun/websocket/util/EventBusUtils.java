package com.baodanyun.websocket.util;

import com.google.common.eventbus.EventBus;

/**
 * Created by liaowuhen on 2017/5/12.
 */

public class EventBusUtils {
    private final static EventBus eventBus = new EventBus();


    public static void post(Object event) {
        eventBus.post(event);
    }


    public static void register(Object handler) {
        eventBus.register(handler);
    }


    public static void unregister(Object handler) {
        eventBus.unregister(handler);
    }
}
