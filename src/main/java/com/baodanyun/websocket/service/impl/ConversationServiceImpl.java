package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.service.ConversationService;
import com.baodanyun.websocket.util.JSONUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liaowuhen on 2017/6/26.
 */

@Service("conversationServiceBat")
public class ConversationServiceImpl implements ConversationService {
    /**
     * 客服 _用户
     */
    public static final Map<String, Map<String, String>> conversations = new ConcurrentHashMap();
    protected static Logger logger = LoggerFactory.getLogger(ConversationServiceImpl.class);

    @Override
    public void clear(String cJid) {
        conversations.remove(cJid);
    }

    @Override
    public Map<String, String> get(String cJid) {
        return conversations.get(cJid);
    }

    @Override
    public String get(String cJid, String vJid) {
        Map<String, String> map = get(cJid);
        if (null != map) {
            map.get(vJid);
        }
        return null;
    }

    @Override
    public void addConversations(String cJid, ConversationMsg cm) {
        logger.info("cJid--[{}]**********key[{}]", cJid, cm);
        Map<String, String> map = conversations.get(cJid);
        if (null == map) {
            map = new HashMap<>();
            conversations.put(cJid, map);
        }
        map.put(cm.getKey(), JSONUtil.toJson(cm));
    }

    @Override
    public void removeConversations(String cJid, String key) {
        logger.info("cJid--[{}]**********key[{}]", cJid, key);
        Map<String, String> map = conversations.get(cJid);
        if (null != map) {
            map.remove(key);
        }
    }

    @Override
    public boolean isExist(String cJid, String key) {
        logger.info("cJid--[{}]**********key[{}]", cJid, key);
        Map<String, String> map = conversations.get(cJid);
        if (null != map) {
            String cm = map.get(key);
            if (StringUtils.isNotEmpty(cm)) {
                logger.info(JSONUtil.toJson(cm));
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRealKey(String cJid) {
        return cJid;
    }

}
