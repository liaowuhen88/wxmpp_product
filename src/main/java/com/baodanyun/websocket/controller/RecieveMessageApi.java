package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.service.*;
import com.baodanyun.websocket.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by yutao on 2016/10/4.
 */
@RestController
public class RecieveMessageApi extends BaseController {
    protected static Logger logger = LoggerFactory.getLogger(RecieveMessageApi.class);

    @Autowired
    private UserServer userServer;

    @Autowired
    private MessageSendToWeixin messageSendToWeixin;

    @Autowired
    private XmppUserOnlineServer xmppUserOnlineServer;

    @Autowired
    @Qualifier("wxUserLifeCycleService")
    private UserLifeCycleService userLifeCycleService;

    @Autowired
    private UserCacheServer userCacheServer;

    @Autowired
    private XmppService xmppService;

    private String me = "当前客服不在线，请点击以下链接留言";
    /**
     * 指定客服关键字
     */
    private String keywords = "@客服";

    @RequestMapping(value = "receiveMsg")
    public void getMessageByCId(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Response response ;
        try {
            String body = HttpServletRequestUtils.getBody(request);
            String base = request.getContextPath();
            Msg msg = msg(body);
            Visitor visitor = userServer.initVisitor(base, msg.getFrom(),null);
            visitor.setType(1);

            logger.info(JSONUtil.toJson(visitor));
            if (!StringUtils.isEmpty(msg.getContent()) && msg.getContent().startsWith(keywords)) {
                response = getBindCustomerResponse(visitor,msg);
            } else {

                if (null != visitor.getCustomer()) {
                    boolean cFlag = xmppService.isAuthenticated(visitor.getCustomer().getId());
                    if (!cFlag) {
                        cFlag = xmppUserOnlineServer.isOnline(msg.getTo());
                    }

                    // 客服不在线
                    if (!cFlag) {
                        String url = request.getRequestURL().toString();
                        response = getLeaveMessageResponse(visitor.getCustomer(),url, msg);
                    } else {
                        boolean flag = xmppService.isAuthenticated(visitor.getId());

                        if (!flag) {
                            userLifeCycleService.login(visitor);
                            userLifeCycleService.online(visitor);
                        }

                        userLifeCycleService.receiveMessage(visitor,JSONUtil.toJson(msg));

                        response = getOnlineResponse();
                    }

                } else {
                    throw new BusinessException("客服不存在");
                }
            }

        } catch (Exception e) {
            logger.error("", e);
            response = new Response();
            response.setMsg(e.getMessage());
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }


    /**
     * 初始化消息
     *
     * @param body
     * @return
     * @throws Exception
     */

    public Msg msg(String body) throws Exception {
        if (StringUtils.isEmpty(body)) {
            throw new BusinessException("body is null");
        }
        logger.info(body);
        Msg msg = JSONUtil.toObject(Msg.class, body);
        /**
         * 设置默认接入客服
         */
        if (StringUtils.isEmpty(msg.getTo())) {
            msg.setTo(Config.controlId);
        }
        return msg;
    }


    public Response getLeaveMessageResponse(AbstractUser customer, String url, Msg msg) {
        Response response = new Response();

        logger.info("customer[" + customer.getId() + "] not online");

        int end = url.indexOf("/api/");
        String base = url.substring(0, end);
        String u = base + "/visitorlogin?t=" + msg.getTo() + "&u=" + msg.getFrom();
        String info = me + "[<a href=\\\"" + u + "\\\">我要留言</a>]";
        logger.info("info:" + info);
        Msg sendMsg = new Msg(info);
        sendMsg.setContentType(Msg.MsgContentType.text.toString());
        Long ct = new Date().getTime();
        sendMsg.setTo(customer.getId());
        sendMsg.setCt(ct);
        messageSendToWeixin.send(sendMsg, msg.getFrom(), customer.getId());
        response.setSuccess(true);

        return response;
    }

    public Response getOnlineResponse() {
        Response response = new Response();

        response.setSuccess(true);
        return response;
    }

    public Response getBindCustomerResponse(Visitor visitor, Msg msg) throws BusinessException, InterruptedException {
        String cJid = msg.getContent().substring(keywords.length()).trim();
        userCacheServer.addVisitorCustomerOpenId(msg.getFrom(), XMPPUtil.nameToJid(cJid));

        Msg sendMsg = new Msg("客服切换到 ["+cJid+"]");

        sendMsg.setContentType(Msg.MsgContentType.text.toString());
        Long ct = new Date().getTime();
        sendMsg.setCt(ct);

        messageSendToWeixin.send(sendMsg, msg.getFrom(), "sys");

        userLifeCycleService.logout(visitor);
       Response response = new Response();
        response.setMsg("change bind customer");
        response.setSuccess(true);

        return response;
    }


}
