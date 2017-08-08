package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.user.ComparatorConversationMsg;
import com.baodanyun.websocket.bean.user.Customer;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.service.ConversationService;
import com.baodanyun.websocket.service.MessageFiterService;
import com.baodanyun.websocket.service.UserCacheServer;
import com.baodanyun.websocket.service.XmppService;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.Render;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by yutao on 2016/10/4.
 */
@RestController
public class QueueApi extends BaseController {
    protected static Logger logger = LoggerFactory.getLogger(CustomerApi.class);

    @Autowired
    private UserCacheServer userCacheServer;

    @Autowired
    private XmppService xmppService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private MessageFiterService messageFiterService;

    @RequestMapping(value = "queue/{q}")
    public void backupQueue(@PathVariable("q") String q, HttpServletRequest request, HttpServletResponse response) {
        Response msgResponse = new Response();
        try {
            Customer customer = (Customer) request.getSession().getAttribute(Common.USER_KEY);
            if (customer != null) {

                Map<String, String> map = conversationService.get(customer.getId());

                if(null != map){
                    List<ConversationMsg> collections = new ArrayList<>();
                    List<String> li = new ArrayList<>(map.values());
                    for (String ob : li) {
                        ConversationMsg cm = JSONUtil.toObject(ConversationMsg.class, ob);
                        boolean isEncrypt = messageFiterService.isEncrypt(customer.getId(), cm.getFrom());
                        if (!isEncrypt) {
                            cm.setOnlineStatus(ConversationMsg.OnlineStatus.encrypt);
                        }
                        collections.add(cm);
                    }
                    ComparatorConversationMsg comparator = new ComparatorConversationMsg();
                    Collections.sort(collections, comparator);
                    msgResponse.setData(collections);
                }
                msgResponse.setSuccess(true);
            } else {
                msgResponse.setMsg("非法访问");
                msgResponse.setSuccess(false);
            }
        } catch (Exception e) {
            logger.error("error", e);
            msgResponse.setSuccess(false);
        }
        Render.r(response, XMPPUtil.buildJson(msgResponse));
    }


    public enum QueueName {
        online("1", "online"), wait("2", "wait");

        private String key;
        private String value;
        QueueName(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public static String getQueueNameByKey(String key) {
            if (StringUtils.isBlank(key)) {
                return null;
            }
            for (QueueName queueName : QueueName.values()) {
                if (key.equals(queueName.getKey())) {
                    return queueName.name();
                }
            }
            return null;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


   /* private Friend getFriend(ConversationMsg cm) {

        Friend friend = new Friend();
        friend.setId(cm.getId());
        friend.setOnlineStatus(Friend.OnlineStatus.online);
        friend.setName(cm.getKey());
        friend.setLoginUsername(cm.getLoginUsername());
        friend.setLoginTime(cm.getCt());
        return friend;

    }*/

    /**
     * Created by yutao on 2016/10/4.
     */
    @RestController
    public static class PresenceTypeApi extends BaseController {
        protected static Logger logger = LoggerFactory.getLogger(PresenceTypeApi.class);

        @Autowired
        private XmppService xmppService;

        @RequestMapping(value = "GetPresenceType")
        public void GetPresenceType(String jid, String to, HttpServletResponse httpServletResponse) {
            Response response = new Response();
            try {
                XMPPConnection conn = xmppService.getXMPPConnection(jid);
                Roster roster = Roster.getInstanceFor(conn);
                Presence presence = roster.getPresence(jid);

                logger.info(JSONUtil.toJson(presence));
                if (presence.getType() == Presence.Type.available) {
                    logger.info("User is online");
                }

                response.setSuccess(true);


            } catch (Exception e) {
                logger.error("", e);
                response.setMsg("update error");
                response.setSuccess(false);
            }
            Render.r(httpServletResponse, JSONUtil.toJson(response));
        }


    }
}
