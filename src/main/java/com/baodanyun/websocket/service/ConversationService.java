package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.msg.ConversationMsg;

import java.util.Map;

/**
 * Created by liaowuhen on 2017/6/26.
 */
public interface ConversationService {

    /**
     * 清除客服所有的会话缓存
     *
     * @param cJid
     */
    void clear(String cJid);

    /**
     * 获取客服所有的会话缓存
     *
     * @param cJid
     */
    Map<String, ConversationMsg> get(String cJid);

    void addConversations(String cJid, ConversationMsg cm);

    void removeConversations(String cJid, String vJid);

    boolean isExist(String cJid, String vJid);

}
