package com.baodanyun.websocket.core.listener;

import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.event.JoinRoomEvent;
import com.baodanyun.websocket.service.DoubaoFriendsService;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.WebSocketService;
import com.baodanyun.websocket.service.XmppService;
import com.baodanyun.websocket.util.EventBusUtils;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.provider.IQProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理xmpp接收到的消息，并且转换成webSocket消息发给自己
 * 离线消息只包括文本，图片类型
 */
public class MUCPacketExtensionProviderAndInitConnectListener extends IQProvider implements ConnectionListener {
    private static Logger logger = LoggerFactory.getLogger(MUCPacketExtensionProviderAndInitConnectListener.class);
    private List<JoinRoomEvent> rooms;
    private WebSocketService webSocketService;
    private MsgSendControl msgSendControl;
    private DoubaoFriendsService doubaoFriendsService;
    private XmppService xmppService;
    private AbstractUser user;

    public MUCPacketExtensionProviderAndInitConnectListener(XmppService xmppService, AbstractUser user, WebSocketService webSocketService, MsgSendControl msgSendControl, DoubaoFriendsService doubaoFriendsService) {
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

        if (null != rooms) {
            for (JoinRoomEvent room : rooms) {
                EventBusUtils.post(room);
            }
            rooms = null;
        }

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

    /**
     * 加自定义会议信息解析
     *
     * @param parser
     * @param i
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     * @throws SmackException
     */
    @Override
    public Element parse(XmlPullParser parser, int i) throws XmlPullParserException, IOException, SmackException {
        int eventType = parser.getEventType();
        List<JoinRoomEvent> rooms = new ArrayList<>();
        while (true) {
            if (eventType == XmlPullParser.START_TAG) {
                if ("room".equals(parser.getName())) {
                    String account = parser.getAttributeValue("", "account");
                    String room = parser.nextText();
                    try {
                        JoinRoomEvent je = new JoinRoomEvent(user, room);
                        rooms.add(je);
                    } catch (Exception e) {
                        logger.error("error", e);
                    }
                    this.rooms = rooms;
                    logger.info("account is " + account + " and room is " + room);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if ("muc".equals(parser.getName())) {
                    break;
                }
            }
            eventType = parser.next();
        }

        return null;
    }
}