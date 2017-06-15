package com.baodanyun.websocket.service;


import com.baodanyun.websocket.bean.msg.Msg;

/**
 * Created by liaowuhen on 2017/5/11.
 */
public interface MsgService {
    Msg getNewRoomJoines(String room, String detail, String to);
}
