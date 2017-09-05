package com.baodanyun.websocket.core.handle;

import com.baodanyun.websocket.service.XmppService;
import com.baodanyun.websocket.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * Created by yutao on 2016/9/27.
 */
public abstract class AbstractWebSocketHandler extends TextWebSocketHandler {

    protected static Logger logger = LoggerFactory.getLogger(AbstractWebSocketHandler.class);
    public XmppService xmppService = SpringContextUtil.getBean("xmppServiceImpl", XmppService.class);

    protected abstract void saveWebSocketSession(String cid, WebSocketSession session);

    protected abstract boolean isClosededWebSocketSession(String cid) throws IOException, InterruptedException;

}
