package com.baodanyun.websocket.core.handle;

import com.baodanyun.websocket.util.CommonConfig;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * 进入当前处理器后
 * 节点已经验证完成了 afterConnectionEstablished  afterConnectionClosed 非一个线程调用
 */
public class MobileCustomerWebSocketHandler extends CustomerWebSocketHandler {
    protected void saveWebSocketSession(String cid, WebSocketSession session) {
        WebSocketSession ws = webSocketService.getWebSocketSession(cid, CommonConfig.MOBILE_CUSTOMER);
        if (null != ws && !ws.getId().equals(session.getId()) && ws.isOpen()) {
            try {
                WebSocketMessage wm = new TextMessage("notHeartBeat");
                session.sendMessage(wm);
            } catch (Exception e) {
                logger.error("error", e);
            }
            logger.info("不是当前窗口，忽略");
        }

        webSocketService.saveSession(cid, CommonConfig.MOBILE_CUSTOMER, session);
    }

    protected boolean isClosededWebSocketSession(String cid) throws IOException, InterruptedException {
        return webSocketService.isCloseded(cid, CommonConfig.MOBILE_CUSTOMER);
    }
}
