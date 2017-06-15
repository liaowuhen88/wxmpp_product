package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.msg.status.StatusMsg;
import com.baodanyun.websocket.service.MsgService;
import com.baodanyun.websocket.util.XMPPUtil;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by liaowuhen on 2017/5/11.
 */

@Service
public class MsgServiceImpl implements MsgService {
    public static void main(String[] args) {
        String room = "xvql187@conference.126xmpp/\u003cspan class\u003d\"emoji emoji1f4a2\"\u003e\u003c/span\u003e          导演\u003cspan class\u003d\"emoji emojiae\"\u003e\u003c/span\u003e";
        String realRoom = XMPPUtil.removeRoomSource(room);
        System.out.println(realRoom);
    }

    @Override
    public Msg getNewRoomJoines(String room, String detail, String to) {
        String realRoom = XMPPUtil.removeRoomSource(room);
        StatusMsg sm = new StatusMsg();
        sm.setStatus(StatusMsg.Status.onlineQueueSuccess);
        sm.setType(Msg.Type.status.toString());
        sm.setLoginTime(new Date().getTime());
        sm.setFromName(detail);
        sm.setFromType(Msg.fromType.group);
        sm.setFrom(realRoom);
        sm.setLoginUsername(realRoom);
        sm.setTo(to);
        sm.setCt(new Date().getTime());

        return sm;
    }

}
