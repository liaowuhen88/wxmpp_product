package com.baodanyun.websocket.core.handle;

import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.service.UserLifeCycleService;
import com.baodanyun.websocket.service.WebSocketService;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.SpringContextUtil;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * 进入当前处理器后
 * 节点已经验证完成了
 */
public class VisitorWebSocketHandler extends AbstractWebSocketHandler {

    WebSocketService webSocketService = SpringContextUtil.getBean("webSocketService", WebSocketService.class);
    UserLifeCycleService userLifeCycleService = SpringContextUtil.getBean("wvUserLifeCycleService", UserLifeCycleService.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Visitor visitor = (Visitor) session.getHandshakeAttributes().get(Common.USER_KEY);
        webSocketService.saveSession(visitor.getId(), session);

        logger.info("ip:[" + session.getLocalAddress() + "]------visitorId:[" + visitor.getId() + "]--------+nickName:[" + visitor.getNickName() + "]  ---- sessionId:[" + session.getId() + "]  session is open");

        userLifeCycleService.online(visitor);

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("thread--->" + Thread.currentThread().getName() + "webSocket receive message:" + JSONUtil.toJson(message));
        try{
            Visitor visitor = (Visitor) session.getHandshakeAttributes().get(Common.USER_KEY);

            String content = message.getPayload();
            userLifeCycleService.receiveMessage(visitor,content);
        }catch (Exception e){
            logger.info("", e);
        }


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Visitor visitor = (Visitor) session.getHandshakeAttributes().get(Common.USER_KEY);
        logger.info("ip:[" + session.getLocalAddress() + "]------visitorId:[" + visitor.getId() + "]--------+nickName:[" + visitor.getNickName() + "] ---- sessionId:[" + session.getId() + "]   session is closed");
        // webSocketService.closed(visitor.getId());
        boolean flag = webSocketService.isCloseded(visitor.getId());
        if(flag){
            logger.info("userLifeCycleService.logout(visitor)------visitorId:[" + visitor.getId() + "]--------+nickName:[" + visitor.getNickName() + "] ---- status:[" +status + "]   session is closed");
            userLifeCycleService.logout(visitor);
        }

    }

}
