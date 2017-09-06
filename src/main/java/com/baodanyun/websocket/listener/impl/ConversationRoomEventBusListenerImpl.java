package com.baodanyun.websocket.listener.impl;


import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.event.ConversationRoomEvent;
import com.baodanyun.websocket.listener.EventBusListener;
import com.baodanyun.websocket.model.Ofmucroom;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.MsgService;
import com.baodanyun.websocket.service.OfmucroomService;
import com.baodanyun.websocket.service.XmppService;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liaowuhen on 2017/5/12.
 */

@Service
public class ConversationRoomEventBusListenerImpl extends AbstarctEventBusListener<ConversationRoomEvent> implements EventBusListener<ConversationRoomEvent> {
    private static Logger logger = LoggerFactory.getLogger(ConversationRoomEventBusListenerImpl.class);

    private Map<String, Ofmucroom> mucs = new ConcurrentHashMap<>();

    @Autowired
    private MsgSendControl msgSendControl;
    @Autowired
    private XmppService xmppService;
    @Autowired
    private MsgService msgService;

    @Autowired
    private OfmucroomService ofmucroomService;

    @Override
    @Subscribe
    public boolean processExpiringEvent(final ConversationRoomEvent joinRoomEvent) {
        logger.info(JSONUtil.toJson(joinRoomEvent));

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String room = joinRoomEvent.getRoom();
                    logger.info(room);
                    if (null != room) {

                        Ofmucroom subject = mucs.get(room);

                        if (null == subject) {
                            subject = ofmucroomService.selectByPrimaryKey((long) 1, XMPPUtil.jidToName(room));
                            if (subject != null) {
                                mucs.put(room, subject);
                            }
                        }

                        Msg msg = msgService.getNewRoomJoines(joinRoomEvent.getRoom(), subject, joinRoomEvent.getUser().getId(), joinRoomEvent.getUser().getAppkey());
                        logger.info(JSONUtil.toJson(msg));
                        msgSendControl.sendMsg(msg);

                    }
                } catch (Exception e) {
                    logger.error("error", e);
                }
            }
        });
        return false;
    }
}
