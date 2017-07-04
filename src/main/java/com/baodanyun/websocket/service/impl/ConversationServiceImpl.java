package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.service.ConversationService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liaowuhen on 2017/6/26.
 */

@Service
public class ConversationServiceImpl implements ConversationService {

    /**
     * 客服 _用户
     */
    public static final Map<String, Set<String>> conversations = new ConcurrentHashMap();

    @Override
    public void clear(String cJid) {
        conversations.remove(cJid);
    }

    @Override
    public void addConversations(String cJid, String vJid) {
        Set<String> sets = conversations.get(cJid);
        if (null == sets) {
            sets = new HashSet<>();
            conversations.put(cJid, sets);
        }
        sets.add(vJid);
    }

    @Override
    public void removeConversations(String cJid, String vJid) {
        Set<String> sets = conversations.get(cJid);
        if (null != sets) {
            sets.remove(vJid);
        }
    }

    @Override
    public boolean isExist(String cJid, String vJid) {
        Set<String> sets = conversations.get(cJid);
        if (null != sets) {
            return sets.contains(vJid);
        }
        return false;
    }

}
