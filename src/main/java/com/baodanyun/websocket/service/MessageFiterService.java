package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.msg.Msg;

/**
 * Created by liaowuhen on 2017/7/31.
 */
public interface MessageFiterService {
    void filter(String jid, Msg msg);

    boolean dispaly(String jid, String from);

    boolean isEncrypt(String jid, String from);
}
