package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.event.ComputationalCostsEvent;
import com.baodanyun.websocket.event.MessageArchiveAdapterEvent;
import com.baodanyun.websocket.model.MessageArchiveAdapter;
import com.baodanyun.websocket.service.impl.JedisServiceImpl;
import com.baodanyun.websocket.util.EventBusUtils;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
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
    private final Map<String, WebSocketSession> WEB_SOCKET_SESSION_MAP = new ConcurrentHashMap<>();
    BlockingQueue<Msg> basket = new LinkedBlockingQueue<>(100);
    /**
     * key 为用户id
     */
    @Autowired
    private XmppService xmppService;
    @Autowired
    private JedisService jedisService;

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
        WEB_SOCKET_SESSION_MAP.put(jid, webSocketSession);
    }

    public WebSocketSession getWebSocketSession(String jid) {
        return WEB_SOCKET_SESSION_MAP.get(jid);
    }

    public boolean isConnected(String jid) {
        WebSocketSession webSocketSession = getWebSocketSession(jid);
        if (null != webSocketSession) {
            if (webSocketSession.isOpen()) {
                return true;
            } else {
                logger.info("jid:[" + jid + "] session is closed");
                return false;
            }
        } else {
            logger.info("jid:[" + jid + "] session is null");
            return false;
        }

    }

    /**
     * @param jid
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private boolean closedAndXmpp(String jid) throws IOException, InterruptedException {
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

    }


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
        WebSocketSession webSocketSession = getWebSocketSession(jid);
        if (null != webSocketSession) {
            if (webSocketSession.isOpen()) {
                webSocketSession.close();
            }
            WEB_SOCKET_SESSION_MAP.remove(jid);
            return true;
        } else {
            logger.info("jid:[" + jid + "] session is closed or session is null");
            return false;
        }
    }

    private boolean send(String jid, Msg msg) {

        try {
            if (isConnected(jid)) {
                WebSocketSession webSocketSession = getWebSocketSession(jid);
                if (Msg.Type.msg.toString().equals(msg.getType())) {
                    String status = jedisService.getFromMap(JedisServiceImpl.displayStatus, msg.getTo());

                    if ("1".equals(status)) {
                        //TODO 计费接口
                        Msg clone = (Msg) SerializationUtils.clone(msg);
                        ComputationalCostsEvent cce = new ComputationalCostsEvent(jid, clone);
                        EventBusUtils.post(cce);

                    } else {
                        /**
                         * 消息加密
                         */
                        MessageArchiveAdapter ma = new MessageArchiveAdapter();
                        ma.setMessageid(msg.getId());
                        ma.setContent(msg.getContent());

                        MessageArchiveAdapterEvent me = new MessageArchiveAdapterEvent();
                        me.setMessageArchiveAdapter(ma);
                        EventBusUtils.post(me);

                        if (Msg.MsgContentType.image.toString().equals(msg.getContentType())) {
                            msg.setContent("您收到一张图片");
                            msg.setContentType(Msg.MsgContentType.text.toString());
                        } else if (Msg.MsgContentType.video.toString().equals(msg.getContentType())) {
                            msg.setContent("您收到一段视频");
                            msg.setContentType(Msg.MsgContentType.text.toString());
                        } else if (Msg.MsgContentType.audio.toString().equals(msg.getContentType())) {
                            msg.setContent("您收到一段音频");
                            msg.setContentType(Msg.MsgContentType.text.toString());
                        } else if (Msg.MsgContentType.text.toString().equals(msg.getContentType())) {
                            msg.setContent("您收到一条消息");
                        }


                    }
                }


                String content = XMPPUtil.buildJson(msg);
                webSocketSession.sendMessage(new TextMessage(content));

                return true;
            }
        } catch (Exception e) {
            logger.error("webSocketSession send error", e);
        }
        return false;
    }
}
