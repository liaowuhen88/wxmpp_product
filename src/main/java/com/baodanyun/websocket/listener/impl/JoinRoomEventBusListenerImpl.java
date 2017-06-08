package com.baodanyun.websocket.listener.impl;


import com.baodanyun.websocket.event.JoinRoomEvent;
import com.baodanyun.websocket.listener.EventBusListener;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.MsgService;
import com.baodanyun.websocket.service.XmppService;
import com.baodanyun.websocket.util.JSONUtil;
import com.google.common.eventbus.Subscribe;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2017/5/12.
 */

@Service
public class JoinRoomEventBusListenerImpl extends AbstarctEventBusListener<JoinRoomEvent> implements EventBusListener<JoinRoomEvent> {
    private static Logger logger = Logger.getLogger(JoinRoomEventBusListenerImpl.class);

    @Autowired
    private MsgSendControl msgSendControl;
    @Autowired
    private XmppService xmppService;
    @Autowired
    private MsgService msgService;

    @Override
    @Subscribe
    public boolean processExpiringEvent(final JoinRoomEvent joinRoomEvent) {
        logger.info(JSONUtil.toJson(joinRoomEvent));

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != joinRoomEvent.getRoom()) {

                        xmppService.joinRoom(joinRoomEvent.getUser(), joinRoomEvent.getRoom());

                       /* Msg msg = msgService.getNewRoomJoines(joinRoomEvent.getRoom(), joinRoomEvent.getUser().getId());
                        msgSendControl.sendMsg(msg);*/

                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        });
        return false;
    }
}
