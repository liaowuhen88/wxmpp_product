package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.user.ComparatorConversationMsg;
import com.baodanyun.websocket.bean.user.Customer;
import com.baodanyun.websocket.bean.user.Friend;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.service.ConversationService;
import com.baodanyun.websocket.service.DoubaoFriendsService;
import com.baodanyun.websocket.service.UserCacheServer;
import com.baodanyun.websocket.service.XmppService;
import com.baodanyun.websocket.util.Render;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
    private DoubaoFriendsService doubaoFriendsService;

    @RequestMapping(value = "queue/{q}")
    public void backupQueue(@PathVariable("q") String q, HttpServletRequest request, HttpServletResponse response) {
        Response msgResponse = new Response();
        try {
            Customer customer = (Customer) request.getSession().getAttribute(Common.USER_KEY);
            if (customer != null) {

                Set<Friend> friendList = new HashSet<>();
                Map<String, ConversationMsg> map = conversationService.get(customer.getId());
                xmppService.getHostRoom(customer.getId());

                /*if (!CollectionUtils.isEmpty(map)) {
                    for (ConversationMsg re : map.values()) {
                            Friend friend = getFriend(re);
                            if (null != friend) {
                                friendList.add(friend);
                            }
                    }
                }*/

                List<ConversationMsg> li = new ArrayList<>(map.values());
                ComparatorConversationMsg comparator = new ComparatorConversationMsg();
                Collections.sort(li, comparator);
                msgResponse.setData(li);
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

}
