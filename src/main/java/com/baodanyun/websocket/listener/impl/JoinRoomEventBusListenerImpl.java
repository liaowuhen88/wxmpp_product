package com.baodanyun.websocket.listener.impl;


import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.event.JoinRoomEvent;
import com.baodanyun.websocket.listener.EventBusListener;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.MsgService;
import com.baodanyun.websocket.service.XmppService;
import com.baodanyun.websocket.util.EventBusUtils;
import com.google.common.eventbus.Subscribe;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liaowuhen on 2017/5/12.
 */

@Service
public class JoinRoomEventBusListenerImpl implements EventBusListener<JoinRoomEvent> {
    private static Logger logger = Logger.getLogger(JoinRoomEventBusListenerImpl.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Autowired
    private MsgSendControl msgSendControl;
    @Autowired
    private XmppService xmppService;
    @Autowired
    private MsgService msgService;

    @Override
    @PostConstruct
    public void init() {
        EventBusUtils.register(this);
    }

    @Override
    @Subscribe
    public boolean processExpiringEvent(final JoinRoomEvent joinRoomEvent) {
        logger.info(joinRoomEvent);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    if(null != joinRoomEvent.getRoom() && joinRoomEvent.getRoom().size() > 0 ){
                        for(String room:joinRoomEvent.getRoom()){
                            Msg msg = msgService.getNewRoomJoines(room, joinRoomEvent.getUser().getId());
                            msgSendControl.sendMsg(msg);
                        }
                    }

                    xmppService.joinRoom(joinRoomEvent.getUser(),joinRoomEvent.getRoom());
                }catch (Exception e){
                    logger.error(e.getMessage());
                }
            }
        });
        return false;
    }
}
