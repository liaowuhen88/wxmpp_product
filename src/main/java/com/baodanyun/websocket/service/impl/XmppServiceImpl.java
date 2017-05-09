package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.XmppAdapter;
import com.baodanyun.websocket.bean.XmppContentMsg;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.core.handle.InitConnectListener;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.factory.XMPPConnectionFactory;
import com.baodanyun.websocket.model.DoubaoFriends;
import com.baodanyun.websocket.service.DoubaoFriendsService;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.WebSocketService;
import com.baodanyun.websocket.service.XmppService;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liaowuhen on 2016/11/11.
 */
@Service
public class XmppServiceImpl implements XmppService {

    public final static String CUSTOMER = "customer";
    public final static String CUSTOMER_WEIXIN = "customer_weixin";
    public final static String VISITOR = "visitor";

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected DoubaoFriendsService doubaoFriendsService;

    @Autowired
    protected MsgSendControl msgSendControl;


    @Autowired
    protected WebSocketService webSocketService;


    /**
     * key 为用户id
     */
    private static final Map<String, XmppAdapter> XMPP_MAP = new ConcurrentHashMap<>();

    public boolean isAuthenticated(String jid) {
        AbstractXMPPConnection xMPPConnection = getXMPPConnection(jid);
        if (null != xMPPConnection) {
            if (xMPPConnection.isConnected() && xMPPConnection.isAuthenticated()) {
                return true;
            } else {
                logger.info("jid:[" + jid + "] xMPPConnection is closed ");
                return false;
            }
        } else {
            logger.info("jid:[" + jid + "]  xMPPConnection is null");
            return false;
        }
    }

    /**
     * 是否在线 包括手机端在线和容器tomcat在线
     *
     * @param jid
     * @return
     */


    public boolean isConnected(String jid) {
        AbstractXMPPConnection xMPPConnection = getXMPPConnection(jid);
        if (null != xMPPConnection && xMPPConnection.isConnected()) {
            return true;
        } else {
            logger.info("jid:[" + jid + "] xMPPConnection is closed or xMPPConnection is null");
            return false;
        }
    }



    public RosterGroup getGroupByName(String jid,String groupName) throws Exception {
        AbstractXMPPConnection connection = this.getXMPPConnectionAuthenticated(jid);
        Roster roster = Roster.getInstanceFor(connection);
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        roster.reload();
        roster.createEntry(jid, jid, new String[]{groupName});
        return  roster.getGroup(groupName);
    }


    public Collection<RosterEntry> getRosterEntrys(String jid,String groupName) throws Exception {
        RosterGroup group = getGroupByName(jid,groupName);
        Collection<RosterEntry> entries = group.getEntries();
        return  entries;
    }


    /**
     * 保存登陆成功的XMppConnection
     *
     * @param jid
     * @param xMPPConnection
     */
    public void saveXMPPConnection(String jid, AbstractXMPPConnection xMPPConnection) {
        XmppAdapter xa = new XmppAdapter();
        xa.setXmpp(xMPPConnection);
        xa.setTime(System.currentTimeMillis());
        XMPP_MAP.put(jid, xa);
    }

    /**
     * 获取登录成功的XMPPConnection
     *
     * @param jid
     * @return
     */

    public AbstractXMPPConnection getXMPPConnectionAuthenticated(String jid) throws BusinessException {
        AbstractXMPPConnection xmppConnection = getXMPPConnection(jid);
        if (null != xmppConnection && xmppConnection.isAuthenticated()) {
            return xmppConnection;
        } else {
            throw new BusinessException("jid:[ " + jid + "----xMPPConnection is not Authenticated");
        }
    }

    /**
     * 获取登录成功的XMPPConnection
     *
     * @param jid
     * @return
     */
    public AbstractXMPPConnection getXMPPConnection(String jid) {
        XmppAdapter xmppAdapter = XMPP_MAP.get(jid);
        if (null != xmppAdapter) {
            return xmppAdapter.getXmpp();
        }
        return null;
    }

    public boolean closed(String jid) throws IOException {
        AbstractXMPPConnection xMPPConnection = getXMPPConnection(jid);
        if (null != xMPPConnection) {
            if (xMPPConnection.isConnected()) {
                xMPPConnection.disconnect();
            }
            XMPP_MAP.remove(jid);
            return true;
        } else {
            logger.info("jid:[" + jid + "] xMPPConnection is closed or xMPPConnection is null");
            return false;
        }

    }

    public void sendPresence(String jid, Presence.Type type) throws SmackException.NotConnectedException {
        Presence presence = new Presence(type);
        AbstractXMPPConnection xMPPConnection = getXMPPConnection(jid);
        xMPPConnection.sendStanza(presence);
    }

    public void sendMessageNoChange(Msg msg) throws SmackException.NotConnectedException, BusinessException {
        Message xmppMsg = new Message();

        xmppMsg.setFrom(msg.getFrom());
        xmppMsg.setType(Message.Type.chat);
        xmppMsg.setBody(msg.getContent());
        xmppMsg.setSubject(msg.getContentType());
        xmppMsg.setTo(msg.getTo());
        sendMessage(xmppMsg);

    }

    public void sendMessage(Msg msg) throws SmackException.NotConnectedException, BusinessException {
        Message xmppMsg = new Message();
        XmppContentMsg xm = new XmppContentMsg();
        DoubaoFriends df = doubaoFriendsService.selectByRealFrom(msg.getFrom(),msg.getTo());

        xm.setContent(msg.getContent());
        xm.setContentType(msg.getContentType());
        xm.setFrom(df.getJid());
        xm.setFromName(msg.getFromName());
        xm.setFromIcon(msg.getIcon());
        xm.setFromType(df.getFriendType());
        xm.setGroupName(df.getFriendGroup());
        xm.setTo(df.getFriendJid());
        xm.setRealFrom(df.getFriendJname());
        msg.setFrom(df.getJid());

        xmppMsg.setFrom(msg.getFrom());
        xmppMsg.setType(Message.Type.chat);
        xmppMsg.setBody(JSONUtil.toJson(xm));
        xmppMsg.setSubject(msg.getContentType());
        xmppMsg.setTo(df.getFriendJid());
        sendMessage(xmppMsg);

    }

    public void sendMessage( Message xmppMsg) throws BusinessException, SmackException.NotConnectedException {
        logger.info("xmpp send message:" + JSONUtil.toJson(xmppMsg));
        XMPPConnection connection = this.getXMPPConnectionAuthenticated(xmppMsg.getFrom());
        ChatManager chatmanager = ChatManager.getInstanceFor(connection);

        Chat newChat = chatmanager.createChat(xmppMsg.getTo());
        newChat.sendMessage(xmppMsg);
    }


    public AbstractXMPPConnection getXMPPConnectionNew() {

        return XMPPConnectionFactory.getXMPPConnectionNew();
    }

    public AbstractXMPPConnection getInitConnectListenerXmpp(AbstractUser user) throws IOException, XMPPException, SmackException {
        //获取一个xmpp
        AbstractXMPPConnection xmppConnection = this.getXMPPConnectionNew();
        ConnectionListener connectionListener = new InitConnectListener(user,webSocketService,msgSendControl,doubaoFriendsService);
        //初始化的连接监听器
        xmppConnection.addConnectionListener(connectionListener);
        xmppConnection.connect();

        return xmppConnection;
    }


    public boolean login(AbstractUser user) throws SmackException, XMPPException, IOException {
        boolean isLoginDone = false;
        AbstractXMPPConnection createAccountConn = null;
        AbstractXMPPConnection xmppConnection = getInitConnectListenerXmpp(user);
        try {
            xmppConnection.login(user.getLoginUsername(), user.getPassWord());

            isLoginDone = true;
            createAccountConn = xmppConnection;
        } catch (Exception e) {
            logger.error("login error", e);
            try {

                AccountManager accountManager = AccountManager.getInstance(xmppConnection);
                accountManager.createAccount(user.getLoginUsername(), user.getPassWord());
                xmppConnection.disconnect();

                createAccountConn  = this.getXMPPConnectionNew();
                InitConnectListener initConnectListenerWebVisitor = new InitConnectListener(user,webSocketService,msgSendControl,doubaoFriendsService);
                createAccountConn.addConnectionListener(initConnectListenerWebVisitor);
                createAccountConn.connect();
                createAccountConn.login(user.getLoginUsername(), user.getPassWord());
                isLoginDone = true;
            } catch (Exception e1) {
                logger.error("nodeLogin error", e1);
                isLoginDone = false;
            }
        }
        if (isLoginDone) {
            logger.info("id:[" + user.getId() + "] login success");
            this.saveXMPPConnection(user.getId(), createAccountConn);
        }
        return isLoginDone;
    }

    public void roster(String vjid, String cjid) throws BusinessException, SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {
        AbstractXMPPConnection createAccountConn = this.getXMPPConnectionAuthenticated(vjid);

        Roster roster = Roster.getInstanceFor(createAccountConn);
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

        roster.createEntry(cjid, XMPPUtil.jidToName(cjid), new String[]{"Friends"});
    }



    /**
     * 修改密码
     *
     * @return
     */


    public boolean changePassword(String jid, String pwd) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException, BusinessException {
        if (isAuthenticated(jid)) {

            AccountManager.getInstance(getXMPPConnectionAuthenticated(jid)).changePassword(pwd);
            return true;
        }
        return false;
    }

    @Override
    public boolean createAccount(String jid,String userName, String password) throws BusinessException, SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        if (isAuthenticated(jid)) {

            AccountManager amgr  = AccountManager.getInstance(getXMPPConnectionAuthenticated(jid));
            amgr.createAccount(userName, password);

            return true;
        }
        return false;
    }


}
