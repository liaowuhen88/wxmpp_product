package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by liaowuhen on 2016/11/24.
 */
@Service
public class WebSocketService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<String, Map<String, WebSocketSession>> WEB_SOCKET_SESSION_MAP = new ConcurrentHashMap<>();
    BlockingQueue<Msg> basket = new LinkedBlockingQueue<>(100);
    /**
     * key 为用户id
     */
    @Autowired
    private XmppService xmppService;
    @Autowired
    private MessageFiterService messageFiterService;

    // 生产消息，放入篮子
    public void produce(Msg msg) throws InterruptedException {
        // put方法放入一个苹果，若basket满了，等到basket有位置
        if (null != msg) {
            //basket.put(msg);
            boolean flag = send(msg.getTo(), msg);
            logger.info("msg---" + JSONUtil.toJson(msg) + "send to webSocket --->:"+flag);
        }else {
            logger.info("msg is null");
        }
    }

    // 消费消息，从篮子中取走
    public Msg consume() throws InterruptedException {
        // take方法取出一个苹果，若basket为空，等到basket有消息为止(获取并移除此队列的头部)
        return basket.take();
    }

    public boolean sendToWebSocket() throws InterruptedException {
        Msg msg = consume();
        boolean flag = send(msg.getTo(), msg);
        logger.info("msg---" + JSONUtil.toJson(msg) + "send to webSocket --->:"+flag);
        return flag;
    }

    public void saveSession(String jid, WebSocketSession webSocketSession) {
        Map<String, WebSocketSession> map = WEB_SOCKET_SESSION_MAP.get(jid);
        if (null == map) {
            map = new HashMap<>();
            WEB_SOCKET_SESSION_MAP.put(jid, map);
        }
        map.put(webSocketSession.getId(), webSocketSession);
    }

    public void removeSession(String jid, WebSocketSession webSocketSession) {
        Map<String, WebSocketSession> map = WEB_SOCKET_SESSION_MAP.get(jid);
        if (null != map) {
            map.remove(webSocketSession.getId());
        }

    }

    public Collection<WebSocketSession> getWebSocketSession(String jid) {
        if (null != WEB_SOCKET_SESSION_MAP.get(jid)) {
            return WEB_SOCKET_SESSION_MAP.get(jid).values();
        }
        return null;
    }

    public boolean isConnected(String jid) {
        Collection<WebSocketSession> webSocketSessions = getWebSocketSession(jid);
        boolean flag = false;
        if (null != webSocketSessions && webSocketSessions.size() > 0) {
            for (WebSocketSession ws : webSocketSessions) {
                if (ws.isOpen()) {
                    return true;
                } else {
                    removeSession(jid, ws);
                }
            }
        } else {
            logger.info("jid:[" + jid + "] session is null");
            return false;
        }

        return flag;
    }

    /**
     * @param jid
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    /*private boolean closedAndXmpp(String jid) throws IOException, InterruptedException {
        // 为了防止刷新 需要延迟关闭
        Thread.sleep(1000);

        if (isConnected(jid)) {
            logger.info("fresh ignore closed");
            return false;
        } else {
            logger.info("closed xmpp and webSocket");
            boolean f1 = closed(jid);
            boolean f2 = xmppService.closed(jid);

            return f1 && f2;
        }

    }*/


    /**
     * @param jid
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean isCloseded(String jid) throws IOException, InterruptedException {
        // 为了防止刷新 需要延迟关闭
        Thread.sleep(1000);

        if (isConnected(jid)) {
            logger.info("fresh ignore closed");
            return false;
        } else {

            return true;
        }

    }

    public boolean closed(String jid) throws IOException {
        Collection<WebSocketSession> webSocketSessions = getWebSocketSession(jid);
        if (null != webSocketSessions && webSocketSessions.size() > 0) {
            for (WebSocketSession ws : webSocketSessions) {
                if (ws.isOpen()) {
                    ws.close();
                }
            }
        }
        WEB_SOCKET_SESSION_MAP.remove(jid);
        return true;
    }

    private boolean send(String jid, Msg msg) {

        try {


            if (Msg.Type.msg.toString().equals(msg.getType())) {
                    messageFiterService.filter(jid, msg);
                }

                String content = XMPPUtil.buildJson(msg);
            Collection<WebSocketSession> webSocketSessions = getWebSocketSession(jid);
            if (null != webSocketSessions && webSocketSessions.size() > 0) {
                for (WebSocketSession ws : webSocketSessions) {
                    if (ws.isOpen()) {
                        ws.sendMessage(new TextMessage(content));
                    }
                }
            }


                return true;

        } catch (Exception e) {
            logger.error("webSocketSession send error", e);
        }
        return false;
    }


    public boolean synchronousMsg(String sessionId, String jid, Msg msg) {
        try {
            String content = XMPPUtil.buildJson(msg);
            Collection<WebSocketSession> webSocketSessions = getWebSocketSession(jid);
            if (null != webSocketSessions && webSocketSessions.size() > 0) {
                for (WebSocketSession ws : webSocketSessions) {
                    if (!sessionId.equals(ws.getId())) {
                        if (ws.isOpen()) {
                            try {
                                ws.sendMessage(new TextMessage(content));
                                logger.info("jid {}--- synchronousMsg send msg {}", jid, JSONUtil.toJson(msg));
                            } catch (Exception e) {
                                logger.error("error", e);
                            }

                        }
                    }
                }
            }
            return true;

        } catch (Exception e) {
            logger.error("webSocketSession send error", e);
        }
        return false;
    }
}
