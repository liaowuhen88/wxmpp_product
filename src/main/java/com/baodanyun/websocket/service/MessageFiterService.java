package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.model.MessageArchiveAdapter;

/**
 * Created by liaowuhen on 2017/7/31.
 */
public interface MessageFiterService {
    void filter(String jid, Msg msg);

    /**
     * 是否加密。加密返回true
     *
     * @param jid
     * @param from
     * @return
     */
    boolean isEncrypt(String jid, String from);

    boolean computationalCosts(String jid, Msg msg);

    boolean computationalCosts(String jid, MessageArchiveAdapter msg);

  /*  boolean isEncrypt(String jid, String from);*/
}
