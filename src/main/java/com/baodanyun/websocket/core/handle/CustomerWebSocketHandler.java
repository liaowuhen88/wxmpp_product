package com.baodanyun.websocket.core.handle;

import com.baodanyun.websocket.bean.user.Customer;
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
 * 节点已经验证完成了 afterConnectionEstablished  afterConnectionClosed 非一个线程调用
 */
public class CustomerWebSocketHandler extends AbstractWebSocketHandler {
    public WebSocketService webSocketService = SpringContextUtil.getBean("webSocketService", WebSocketService.class);
    UserLifeCycleService userLifeCycleService = SpringContextUtil.getBean("wcUserLifeCycleService", UserLifeCycleService.class);


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Customer customer = (Customer) session.getHandshakeAttributes().get(Common.USER_KEY);
        webSocketService.saveSession(customer.getId(), session);
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
            userLifeCycleService.receiveMessage(customer, content);
        } catch (Exception e) {
            logger.info(e);
        }


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        Customer customer = (Customer) session.getHandshakeAttributes().get(Common.USER_KEY);
        logger.info("customer session is closed: id[" + customer.getId() + "]" + session);

        boolean flag = webSocketService.isCloseded(customer.getId());

        if (flag) {
            logger.info("userLifeCycleService.logout(customer): id[" + customer.getId() + "]" + status);
        }

    }
}
