package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.service.ConversationService;
import com.baodanyun.websocket.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liaowuhen on 2017/6/26.
 */

@Service
public class ConversationServiceImpl implements ConversationService {
    /**
     * 客服 _用户
     */
    public static final Map<String, Map<String, ConversationMsg>> conversations = new ConcurrentHashMap();
    protected static Logger logger = LoggerFactory.getLogger(ConversationServiceImpl.class);

    @Override
    public void clear(String cJid) {
        conversations.remove(cJid);
    }

    @Override
    public Map<String, ConversationMsg> get(String cJid) {
        return conversations.get(cJid);
    }

    @Override
    public void addConversations(String cJid, ConversationMsg cm) {
        logger.info("cJid--[{}]**********key[{}]", cJid, cm);
        Map<String, ConversationMsg> map = conversations.get(cJid);
        if (null == map) {
            map = new HashMap<>();
            conversations.put(cJid, map);
        }
        map.put(cm.getKey(), cm);
    }

    @Override
    public void removeConversations(String cJid, String key) {
        logger.info("cJid--[{}]**********key[{}]", cJid, key);
        Map<String, ConversationMsg> map = conversations.get(cJid);
        if (null != map) {
            map.remove(key);
        }
    }

    @Override
    public boolean isExist(String cJid, String key) {
        logger.info("cJid--[{}]**********key[{}]", cJid, key);
        Map<String, ConversationMsg> map = conversations.get(cJid);
        if (null != map) {
            ConversationMsg cm = map.get(key);
            if (null != cm) {
                logger.info(JSONUtil.toJson(cm));
                return true;
            }
        }
        return false;
    }

}
