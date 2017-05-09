package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.user.ComparatorFriend;
import com.baodanyun.websocket.bean.user.Customer;
import com.baodanyun.websocket.bean.user.Friend;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.model.DoubaoFriends;
import com.baodanyun.websocket.service.DoubaoFriendsService;
import com.baodanyun.websocket.service.UserCacheServer;
import com.baodanyun.websocket.service.XmppService;
import com.baodanyun.websocket.util.Render;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
    protected static Logger logger = Logger.getLogger(CustomerApi.class);

    @Autowired
    private UserCacheServer userCacheServer;

    @Autowired
    private XmppService xmppService;

    @Autowired
    private DoubaoFriendsService doubaoFriendsService;

    public enum QueueName {
        online("1", "online"), wait("2", "wait");

        QueueName(String key, String value) {
            this.key = key;
            this.value = value;
        }

        private String key;
        private String value;

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
    }


    @RequestMapping(value = "queue/{q}")
    public void backupQueue(@PathVariable("q") String q, HttpServletRequest request, HttpServletResponse response) {
        Response msgResponse = new Response();
        try {
            Customer customer = (Customer) request.getSession().getAttribute(Common.USER_KEY);
            if (customer != null) {

                Set<Friend> friendList = new HashSet<>();

                Collection<DoubaoFriends> res = doubaoFriendsService.selectByJid(customer.getId());

                if (!CollectionUtils.isEmpty(res)) {
                    for (DoubaoFriends re : res) {
                            Friend friend = getFriend(re);
                            if (null != friend) {
                                friendList.add(friend);
                            }
                    }
                }

                List<Friend> li = new ArrayList<>(friendList);
                ComparatorFriend comparator=new ComparatorFriend();
                Collections.sort(li, comparator);
                msgResponse.setData(li);
                msgResponse.setSuccess(true);
            } else {
                msgResponse.setMsg("非法访问");
                msgResponse.setSuccess(false);
            }
        } catch (Exception e) {
            logger.error(e);
            msgResponse.setSuccess(false);
        }
        Render.r(response, XMPPUtil.buildJson(msgResponse));
    }


    private Friend getFriend(DoubaoFriends re) {

        Friend friend = new Friend();
        friend.setId(re.getFriendJname());
        friend.setOnlineStatus(Friend.OnlineStatus.online);
        friend.setName(re.getRealGroupName());
        friend.setLoginUsername(re.getFriendGroup());

        friend.setLoginTime(re.getCt().getTime());
        return friend;

    }

}
