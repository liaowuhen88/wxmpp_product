package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.user.ComparatorConversationMsg;
import com.baodanyun.websocket.bean.user.Customer;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.service.ConversationService;
import com.baodanyun.websocket.service.MessageFiterService;
import com.baodanyun.websocket.util.JSONUtil;
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

                        collections.add(cm);
                    }

                    messageFiterService.initCollections(customer.getAppkey(), customer.getId(), collections);
                    conversationService.isOnline(customer.getAppkey(), customer.getId(), collections);

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

    @RequestMapping(value = "conversationInit")
    public void conversationInit(String vjid, HttpServletRequest request, HttpServletResponse response) {
        Response msgResponse = new Response();
        try {
            Customer customer = (Customer) request.getSession().getAttribute(Common.USER_KEY);
            if (customer != null) {
                String json = conversationService.get(customer.getId(), vjid);
                if (StringUtils.isNotEmpty(json)) {
                    ConversationMsg conversation = JSONUtil.toObject(ConversationMsg.class, json);
                    conversation.setCt(System.currentTimeMillis());
                    conversationService.addConversations(customer.getId(), conversation);
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
}
