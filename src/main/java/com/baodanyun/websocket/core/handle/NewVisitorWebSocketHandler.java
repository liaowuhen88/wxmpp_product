package com.baodanyun.websocket.core.handle;


import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.service.AppKeyService;
import com.baodanyun.websocket.service.ConversationService;
import com.baodanyun.websocket.service.MsgService;
import com.baodanyun.websocket.util.CommonConfig;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.SpringContextUtil;
import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * p
 * 进入当前处理器后
 * 节点已经验证完成了
 */
public class NewVisitorWebSocketHandler extends VisitorWebSocketHandler {
    //   sessionId  token
    private static Map<String, String> sessions = new ConcurrentHashMap<>();
    AppKeyService appKeyService = SpringContextUtil.getBean("appKeyServiceImpl", AppKeyService.class);
    MsgService msgService = SpringContextUtil.getBean("msgServiceImpl", MsgService.class);
    ConversationService conversationService = SpringContextUtil.getBean("conversationService", ConversationService.class);

    protected void saveWebSocketSession(String cid, WebSocketSession session) {
        webSocketService.saveSession(cid, CommonConfig.PC_CUSTOMER, session);
    }

    @Override
    protected boolean isClosededWebSocketSession(String cid) throws IOException, InterruptedException {
        return webSocketService.isCloseded(cid, CommonConfig.PC_CUSTOMER);
    }

    private void init(String sendToken, String jid, Visitor visitor, WebSocketSession session) throws BusinessException, InterruptedException, XMPPException, IOException, SmackException {
        String token = sessions.get(session.getId());
        logger.info("session[" + session.getId() + "]  token {} cachetoken{}", sendToken, token);

        if (null == visitor) {
            throw new BusinessException("token is error");
        }

        if (StringUtils.isEmpty(token)) {
            if (!xmppService.isAuthenticated(visitor.getId())) {
                userLifeCycleService.login(visitor);
                userLifeCycleService.online(visitor);
            }
            sessions.put(session.getId(), sendToken);
            saveWebSocketSession(visitor.getId(), session);
        }

        boolean isExist = conversationService.isExist(jid, visitor.getId());
        if (isExist) {
            logger.info(" user {}, room {} isExist", jid, visitor.getId());
        } else {
            logger.info(" user {}, room {} notExist", jid, visitor.getId());

            ConversationMsg msgConversation = msgService.getNewWebJoines(visitor, jid);
            logger.info(JSONUtil.toJson(msgConversation));
        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("session is open --- ip:[" + session.getLocalAddress() + "]---- sessionId:[" + session.getId() + "]  ");

    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("sessionId--->" + session.getId() + "webSocket receive message:" + JSONUtil.toJson(message));
        String content = message.getPayload();
        Visitor au = null;
        try {
            if (!StringUtils.isEmpty(content)) {
                Msg msg = Msg.handelMsg(content);
                if (!StringUtils.isEmpty(msg.getToken())) {
                    au = appKeyService.getVisitorByToken(msg.getToken());
                    init(msg.getToken(), msg.getTo(), au, session);
                } else {
                    logger.info("token is null");
                }
                if (StringUtils.isNotEmpty(content)) {
                    userLifeCycleService.receiveMessage(au, content);
                }
            }
        } catch (Exception e) {
            logger.error("error", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
    }

}
