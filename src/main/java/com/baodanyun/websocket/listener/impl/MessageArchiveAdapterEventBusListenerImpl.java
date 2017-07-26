package com.baodanyun.websocket.listener.impl;


import com.baodanyun.websocket.event.MessageArchiveAdapterEvent;
import com.baodanyun.websocket.listener.EventBusListener;
import com.baodanyun.websocket.service.MessageArchiveAdapterService;
import com.baodanyun.websocket.util.JSONUtil;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2017/5/12.
 * <p/>
 * 通知客户端有新的会话
 */

@Service
public class MessageArchiveAdapterEventBusListenerImpl extends AbstarctEventBusListener<MessageArchiveAdapterEvent> implements EventBusListener<MessageArchiveAdapterEvent> {

    private static Logger logger = LoggerFactory.getLogger(MessageArchiveAdapterEventBusListenerImpl.class);

    @Autowired
    private MessageArchiveAdapterService messageArchiveAdapterService;

    @Override
    @Subscribe
    public boolean processExpiringEvent(final MessageArchiveAdapterEvent messageArchiveAdapterEvent) {
        logger.info(JSONUtil.toJson(messageArchiveAdapterEvent));

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    messageArchiveAdapterService.insert(messageArchiveAdapterEvent.getMessageArchiveAdapter());

                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        });
        return false;
    }

    private String getKey() {
        return "computationalCosts";
    }

}
