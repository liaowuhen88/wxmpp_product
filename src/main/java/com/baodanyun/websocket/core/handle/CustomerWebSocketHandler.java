package com.baodanyun.websocket.core.handle;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.Customer;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.service.UserLifeCycleService;
import com.baodanyun.websocket.service.WebSocketService;
import com.baodanyun.websocket.util.CommonConfig;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.SpringContextUtil;
import org.apache.commons.lang.SerializationUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * 进入当前处理器后
 * 节点已经验证完成了 afterConnectionEstablished  afterConnectionClosed 非一个线程调用
 */
public class CustomerWebSocketHandler extends AbstractWebSocketHandler {
    public WebSocketService webSocketService = SpringContextUtil.getBean("webSocketService", WebSocketService.class);
    UserLifeCycleService userLifeCycleService = SpringContextUtil.getBean("wcUserLifeCycleService", UserLifeCycleService.class);

    protected void saveWebSocketSession(String cid, WebSocketSession session) {
        webSocketService.saveSession(cid, CommonConfig.PC_CUSTOMER, session);
    }

    protected boolean isClosededWebSocketSession(String cid) throws IOException, InterruptedException {
        return webSocketService.isCloseded(cid, CommonConfig.PC_CUSTOMER);
    }

    protected boolean isConnected(String cid) throws IOException, InterruptedException {
        return webSocketService.isConnected(cid, CommonConfig.PC_CUSTOMER);
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Customer customer = (Customer) session.getHandshakeAttributes().get(Common.USER_KEY);
        saveWebSocketSession(customer.getId(), session);
        //获取一个customerNode节点
        try {
            userLifeCycleService.login(customer);
            userLifeCycleService.online(customer);
        } catch (Exception e) {
            userLifeCycleService.logout(customer);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            Customer customer = (Customer) session.getHandshakeAttributes().get(Common.USER_KEY);
            logger.info("webSocket receive message:" + JSONUtil.toJson(message));
            String content = message.getPayload();
            if ("HeartBeat".equals(content)) {
                WebSocketSession ws = webSocketService.getWebSocketSession(customer.getId(), CommonConfig.PC_CUSTOMER);
                if (ws.getId().equals(session.getId())) {
                    WebSocketMessage wm = new TextMessage("HeartBeat");
                    session.sendMessage(wm);
                } else {
                    WebSocketMessage wm = new TextMessage("notHeartBeat");
                    session.sendMessage(wm);
                    logger.info("不是当前窗口，忽略");
                }
                return;
            }
            Msg msg = userLifeCycleService.receiveMessage(customer, content);
            Msg clone = (Msg) SerializationUtils.clone(msg);
            clone.setFrom(msg.getTo());
            clone.setTo(msg.getFrom());
            clone.setFromType(Msg.fromType.synchronize);
            webSocketService.synchronousMsg(session.getId(), customer.getId(), clone);

        } catch (Exception e) {
            logger.info("", e);
        }


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        Customer customer = (Customer) session.getHandshakeAttributes().get(Common.USER_KEY);
        logger.info("customer session is closed: id[" + customer.getId() + "]" + session);

        boolean flag = isClosededWebSocketSession(customer.getId());

        if (flag) {
            userLifeCycleService.logout(customer);
            logger.info("userLifeCycleService.logout(customer): id[" + customer.getId() + "]" + status);
        }

    }
}
