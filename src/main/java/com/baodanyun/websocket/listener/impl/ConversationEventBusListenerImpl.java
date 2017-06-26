package com.baodanyun.websocket.listener.impl;


import com.baodanyun.websocket.event.ConversationEvent;
import com.baodanyun.websocket.listener.EventBusListener;
import com.baodanyun.websocket.service.XmppService;
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
public class ConversationEventBusListenerImpl extends AbstarctEventBusListener<ConversationEvent> implements EventBusListener<ConversationEvent> {

    private static Logger logger = LoggerFactory.getLogger(ConversationEventBusListenerImpl.class);
    @Autowired
    private XmppService xmppService;

    @Override
    @Subscribe
    public boolean processExpiringEvent(final ConversationEvent conversationEvent) {
        logger.info(conversationEvent.toString());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {


                    //conversationEvent.getMsgSendControl().sendMsg(sm);

                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        });
        return false;
    }


}
