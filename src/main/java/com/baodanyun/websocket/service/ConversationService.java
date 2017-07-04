package com.baodanyun.websocket.service;

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

    void addConversations(String cJid, String vJid);

    void removeConversations(String cJid, String vJid);

    boolean isExist(String cJid, String vJid);

}
