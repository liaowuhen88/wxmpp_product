package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.msg.status.StatusMsg;
import com.baodanyun.websocket.service.MsgService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by liaowuhen on 2017/5/11.
 */

@Service
public class MsgServiceImpl implements MsgService {
    @Override
    public Msg getNewRoomJoines(String room,String to){
        StatusMsg sm = new StatusMsg();
        sm.setStatus(StatusMsg.Status.onlineQueueSuccess);
        sm.setType(Msg.Type.status.toString());
        sm.setLoginTime(new Date().getTime());
        sm.setFromName("群消息");
        sm.setFromType(Msg.fromType.group);
        sm.setFrom(room);
        sm.setLoginUsername(room);
        sm.setTo(to);
        sm.setCt(new Date().getTime());

        return sm;
    }
}
