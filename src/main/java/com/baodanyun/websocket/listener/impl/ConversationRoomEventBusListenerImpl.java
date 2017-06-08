package com.baodanyun.websocket.listener.impl;


import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.event.ConversationRoomEvent;
import com.baodanyun.websocket.listener.EventBusListener;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.MsgService;
import com.baodanyun.websocket.service.XmppService;
import com.google.common.eventbus.Subscribe;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2017/5/12.
 */

@Service
public class ConversationRoomEventBusListenerImpl extends AbstarctEventBusListener<ConversationRoomEvent> implements EventBusListener<ConversationRoomEvent> {
    private static Logger logger = Logger.getLogger(ConversationRoomEventBusListenerImpl.class);

    @Autowired
    private MsgSendControl msgSendControl;
    @Autowired
    private XmppService xmppService;
    @Autowired
    private MsgService msgService;

    @Override
    @Subscribe
    public boolean processExpiringEvent(final ConversationRoomEvent joinRoomEvent) {
        logger.info(joinRoomEvent);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != joinRoomEvent.getRoom()) {

                        Msg msg = msgService.getNewRoomJoines(joinRoomEvent.getRoom(), joinRoomEvent.getUser().getId());
                        msgSendControl.sendMsg(msg);

                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
        return false;
    }
}
