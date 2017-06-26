package com.baodanyun.websocket.service;


import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;

/**
 * Created by liaowuhen on 2017/5/11.
 */
public interface MsgService {
    Msg getNewRoomJoines(String room, String detail, String to);

    Msg getNewPersionalJoines(String realFrom, AbstractUser user, Msg cloneMsg);
}
