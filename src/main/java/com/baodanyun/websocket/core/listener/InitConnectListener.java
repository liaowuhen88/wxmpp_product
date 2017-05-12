package com.baodanyun.websocket.core.listener;

import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.service.DoubaoFriendsService;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.WebSocketService;
import com.baodanyun.websocket.service.XmppService;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

/**
 * 处理xmpp接收到的消息，并且转换成webSocket消息发给自己
 * 离线消息只包括文本，图片类型
 */
public class InitConnectListener implements ConnectionListener {
    private static Logger logger = Logger.getLogger(InitConnectListener.class);
    private WebSocketService webSocketService;
    private MsgSendControl msgSendControl;
    private DoubaoFriendsService doubaoFriendsService;
    private XmppService xmppService;
    private AbstractUser user;
    private XMPPConnection xmppConnection;


    public InitConnectListener(XmppService xmppService,AbstractUser user,WebSocketService webSocketService,MsgSendControl msgSendControl,DoubaoFriendsService doubaoFriendsService) {
        this.xmppService = xmppService;
        this.user = user;
        this.webSocketService = webSocketService;
        this.msgSendControl = msgSendControl;
        this.doubaoFriendsService = doubaoFriendsService;
    }

    @Override
    public void connected(XMPPConnection xmppConnection) {
        xmppService.saveXMPPConnection(user.getId(), xmppConnection);
        logger.info("connected");
    }


    @Override
    public void authenticated(XMPPConnection xmppConnection, boolean b) {
        xmppService.saveXMPPConnection(user.getId(), xmppConnection);
        logger.info("authenticated");
    }

    @Override
    public void connectionClosed() {
        logger.error(user.getLoginUsername() + ":connectionClosed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        if (null != user) {
            logger.error(user.getLoginUsername() + ":connectionClosedOnError", e);
        } else {
            logger.error(":connectionClosedOnError", e);

        }

    }

    @Override
    public void reconnectionSuccessful() {
        logger.warn("reconnectionSuccessful");
    }

    @Override
    public void reconnectingIn(int i) {
        if (null != user) {
            logger.warn(user.getLoginUsername() + "reconnectingIn:" + i);
        } else {
            logger.warn("reconnectingIn:" + i);
        }
    }

    @Override
    public void reconnectionFailed(Exception e) {
        if (null != user) {
            logger.error(user.getLoginUsername() + "reconnectionFailed", e);
        } else {
            logger.error("reconnectionFailed", e);
        }
    }
}