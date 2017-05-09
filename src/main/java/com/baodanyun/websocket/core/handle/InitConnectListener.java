package com.baodanyun.websocket.core.handle;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.service.DoubaoFriendsService;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.WebSocketService;
import com.baodanyun.websocket.util.JSONUtil;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

/**
 * 处理xmpp接收到的消息，并且转换成webSocket消息发给自己
 * 离线消息只包括文本，图片类型
 */
public class InitConnectListener implements ConnectionListener {
    private static Logger logger = Logger.getLogger(InitConnectListener.class);
    private WebSocketService webSocketService;
    private MsgSendControl msgSendControl;
    private DoubaoFriendsService doubaoFriendsService;

    private AbstractUser user;
    private XMPPConnection xmppConnection;


    public InitConnectListener(AbstractUser user,WebSocketService webSocketService,MsgSendControl msgSendControl,DoubaoFriendsService doubaoFriendsService) {
        this.user = user;
        this.webSocketService = webSocketService;
        this.msgSendControl = msgSendControl;
        this.doubaoFriendsService = doubaoFriendsService;
    }

    @Override
    public void connected(XMPPConnection xmppConnection) {
        try {
            addMessageListener(xmppConnection);
        } catch (Exception e) {
            logger.info(e);
        }

    }


    @Override
    public void authenticated(XMPPConnection xmppConnection, boolean b) {
        this.xmppConnection = xmppConnection;
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
            try {
                logger.info("xmpp receive message :" + JSONUtil.toJson(msg));
                Msg sendMsg = msgSendControl.getMsg(msg);
                doubaoFriendsService.dealFrinds(sendMsg);
               /* sendMsg.setTo(InitConnectListener.this.user.getId());
                sendMsg.setFrom("100@"+sendMsg.getFrom());
                Roster roster = Roster.getInstanceFor(InitConnectListener.this.xmppConnection);
                roster.setRosterLoadedAtLogin(true);
                try {
                    roster.createEntry(sendMsg.getFrom(), XMPPUtil.jidToName(sendMsg.getFrom()), new String[]{"friends"});
                } catch (Exception e) {
                    logger.error(" roster.createEntry");
                }*/

                if (null != sendMsg) {
                    // 手机app端发送过来的数据subject 为空
                    sendMsg.setOpenId(user.getOpenId());
                    msgSendControl.sendMsg(sendMsg);
                }
            } catch (InterruptedException e) {
                logger.error("msgSendControl.sendMsg error", e);
            } catch (BusinessException e) {
                logger.error("msgSendControl.sendMsg error", e);
            }
        }
    }
}