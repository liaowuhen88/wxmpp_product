package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.response.FriendAndGroupResponse;
import com.baodanyun.websocket.service.ConversationService;
import com.baodanyun.websocket.service.FriendAndGroupService;
import com.baodanyun.websocket.service.JedisService;
import com.baodanyun.websocket.util.BaseUtil;
import com.baodanyun.websocket.util.CommonConfig;
import com.baodanyun.websocket.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    @Autowired
    private FriendAndGroupService friendAndGroupService;

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

    /**
     * jid 为群或者个人的jid
     *
     * @param appKey
     * @param jid
     * @return
     */
    @Override
    public boolean isOnline(String appKey, String cjid, String jid) {
        try {
            String jids = jedisService.getFromMap(CommonConfig.ZX_CJ_INFO, cjid);
            Map<String, String> maps = JSONUtil.toObject(Map.class, jids);
            List<FriendAndGroupResponse> fgrs = friendAndGroupService.get(appKey, "");

            return isOnline(fgrs, maps, jid);

        } catch (Exception e) {
            logger.error("error", e);
        }

        return false;
    }

    public boolean isOnline(List<FriendAndGroupResponse> fgrs, Map maps, String jid) {
        FriendAndGroupResponse fgrSearch = new FriendAndGroupResponse();
        FriendAndGroupResponse.BasicNode bn = fgrSearch.new BasicNode();
        bn.setJid(jid);
        if (null != fgrs) {
            for (FriendAndGroupResponse fgr : fgrs) {
                boolean flag = fgr.getFriend().contains(bn);
                if (!flag) {
                    flag = fgr.getQungroup().contains(bn);
                }

                if (flag) {
                    Object id = maps.get(fgr.getUsername());
                    Integer redi = BaseUtil.ObjectToInt(id);

                    logger.info("id:{} *** redi:{}", id, redi);

                    if (null != maps && null != redi) {
                        return redi.equals(1);
                    }
                } else {
                    logger.info("not jid{}", jid);
                }
            }
        } else {
            logger.info("fgrs is null");
        }

        return false;
    }

    @Override
    public void isOnline(String appKey, String cjid, List<ConversationMsg> cms) throws Exception {
        String jids = jedisService.getFromMap(CommonConfig.ZX_CJ_INFO, cjid);
        Map<String, String> maps = JSONUtil.toObject(Map.class, jids);
        List<FriendAndGroupResponse> fgrs = null;
        try {
            fgrs = friendAndGroupService.get(appKey, "");
        } catch (Exception e) {
            logger.error("error", e);
        }

        logger.info(JSONUtil.toJson(maps));
        logger.info(JSONUtil.toJson(fgrs));

        if (null != cms) {
            for (ConversationMsg cm : cms) {
                boolean isOnline = isOnline(fgrs, maps, cm.getKey());
                if (isOnline) {
                    cm.setOnlineStatus(ConversationMsg.OnlineStatus.online);
                } else {
                    cm.setOnlineStatus(ConversationMsg.OnlineStatus.history);
                }
            }
        }
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
