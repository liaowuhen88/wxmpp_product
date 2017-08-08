package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.service.ConversationService;
import com.baodanyun.websocket.service.JedisService;
import com.baodanyun.websocket.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by liaowuhen on 2017/6/26.
 */

@Service("conversationService")
public class JedisConversationServiceImpl implements ConversationService {
    private final static String CONVERSATION = "Conversation_";
    /**
     * 客服 _用户
     */
    protected static Logger logger = LoggerFactory.getLogger(JedisConversationServiceImpl.class);

    @Autowired
    private JedisService jedisService;

    @Override
    public void clear(String cJid) {
        jedisService.removeKey(getRealKey(cJid));
    }

    @Override
    public Map<String, String> get(String cJid) {
        Map<String, String> maps = jedisService.getAllFromMap(getRealKey(cJid));
        return maps;
    }

    @Override
    public String get(String cJid, String vJid) throws Exception {
        return jedisService.getFromMap(getRealKey(cJid), vJid);
    }

    @Override
    public void addConversations(String cJid, ConversationMsg cm) {
        logger.info("cJid--[{}]**********key[{}]", cJid, cm);
        jedisService.addMap(getRealKey(cJid), cm.getKey(), JSONUtil.toJson(cm));
    }

    @Override
    public void removeConversations(String cJid, String key) {
        logger.info("cJid--[{}]**********key[{}]", cJid, key);
        jedisService.removeFromMap(getRealKey(cJid), key);
    }

    @Override
    public boolean isExist(String cJid, String key) {
        logger.info("cJid--[{}]**********key[{}]", cJid, key);
        return jedisService.isExitMap(getRealKey(cJid), key);
    }

    @Override
    public String getRealKey(String cJid) {
        return CONVERSATION + cJid;
    }

}
