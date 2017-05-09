package com.baodanyun.wxmpp.test;

import com.baodanyun.websocket.util.JSONUtil;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liaowuhen on 2016/11/10.
 */
public class ConnectionListenerTest implements ConnectionListener {
    protected static final Logger logger = LoggerFactory.getLogger(ConnectionListenerTest.class);

    @Override
    public void connected(XMPPConnection xmppConnection) {

        try {
            addMessageListener(xmppConnection);
        } catch (Exception e) {
            logger.info(e.toString());
        }

        logger.info("connected:" + xmppConnection.getStreamId());
    }


    public void addMessageListener(XMPPConnection xmppConnection) {
        ChatManager chatmanager = ChatManager.getInstanceFor(xmppConnection);
        chatmanager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean b) {
                chat.addMessageListener(new ChatMessageDeal());
            }
        });
    }


    class ChatMessageDeal implements ChatMessageListener {
        @Override
        public void processMessage(Chat chat, Message msg) {
            logger.info("xmpp receive message :" + JSONUtil.toJson(msg));
        }
    }
    @Override
    public void authenticated(XMPPConnection xmppConnection, boolean b) {
        logger.info("authenticated:-->" + xmppConnection.getUser());
    }

    @Override
    public void connectionClosed() {
        logger.info("connectionClosed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        logger.info("connectionClosedOnError");
    }

    @Override
    public void reconnectionSuccessful() {
        logger.info("reconnectionSuccessful");
    }

    @Override
    public void reconnectingIn(int i) {
        logger.info("reconnectingIn");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        logger.info("reconnectionFailed");
    }
}
