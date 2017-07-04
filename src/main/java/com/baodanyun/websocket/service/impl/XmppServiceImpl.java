package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.XmppAdapter;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.core.listener.*;
import com.baodanyun.websocket.dao.OfuserMapper;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.Ofuser;
import com.baodanyun.websocket.service.*;
import com.baodanyun.websocket.util.Config;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liaowuhen on 2016/11/11.
 */
@Service
public class XmppServiceImpl implements XmppService {

    public final static String CUSTOMER = "customer";
    public final static String CUSTOMER_WEIXIN = "customer_weixin";
    public final static String VISITOR = "visitor";
    /**
     * key 为用户id
     */
    private static final Map<String, XmppAdapter> XMPP_MAP = new ConcurrentHashMap<>();
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected DoubaoFriendsService doubaoFriendsService;
    @Autowired
    protected MsgSendControl msgSendControl;

    @Autowired
    protected WebSocketService webSocketService;

    @Autowired
    protected OfuserMapper ofuserMapper;

    @Autowired
    protected ConversationService conversationService;
    @Autowired
    private OfmucroomService ofmucroomService;
    @Autowired
    private MsgService msgService;


    public boolean isAuthenticated(String jid) {
        XMPPConnection xMPPConnection = getXMPPConnection(jid);
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
        XMPPConnection xMPPConnection = getXMPPConnection(jid);
        if (null != xMPPConnection && xMPPConnection.isConnected()) {
            return true;
        } else {
            logger.info("jid:[" + jid + "] xMPPConnection is closed or xMPPConnection is null");
            return false;
        }
    }


    public RosterGroup getGroupByName(String jid, String groupName) throws Exception {
        XMPPConnection connection = this.getXMPPConnectionAuthenticated(jid);
        Roster roster = Roster.getInstanceFor(connection);
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        roster.reload();
        roster.createEntry(jid, jid, new String[]{groupName});
        return roster.getGroup(groupName);
    }


    public Collection<RosterEntry> getRosterEntrys(String jid, String groupName) throws Exception {
        RosterGroup group = getGroupByName(jid, groupName);
        Collection<RosterEntry> entries = group.getEntries();
        return entries;
    }


    /**
     * 保存登陆成功的XMppConnection
     *
     * @param jid
     * @param xMPPConnection
     */
    public void saveXMPPConnection(String jid, XMPPConnection xMPPConnection) {
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

    public XMPPConnection getXMPPConnectionAuthenticated(String jid) throws BusinessException {
        XMPPConnection xmppConnection = getXMPPConnection(jid);
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
    public XMPPConnection getXMPPConnection(String jid) {
        XmppAdapter xmppAdapter = XMPP_MAP.get(jid);
        if (null != xmppAdapter) {
            return xmppAdapter.getXmpp();
        }
        return null;
    }

    public boolean closed(String jid) throws IOException {
        XMPPConnection xMPPConnection = getXMPPConnection(jid);
        if (null != xMPPConnection) {
            if (xMPPConnection.isConnected()) {
                AbstractXMPPConnection axx = (AbstractXMPPConnection) xMPPConnection;
                axx.disconnect();
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
        XMPPConnection xMPPConnection = getXMPPConnection(jid);
        xMPPConnection.sendStanza(presence);
    }

    public void sendMessageNoChange(Msg msg) throws SmackException.NotConnectedException, BusinessException {
        Message xmppMsg = new Message();

        xmppMsg.setFrom(msg.getFrom());

        if (null != msg.getFromType() && Msg.fromType.group.toString().equals(msg.getFromType().toString())) {
            xmppMsg.setType(Message.Type.groupchat);
        } else {
            xmppMsg.setType(Message.Type.chat);
        }

        xmppMsg.setBody(msg.getContent());
        xmppMsg.setSubject(msg.getContentType());
        xmppMsg.setTo(msg.getTo());
        sendMessage(xmppMsg);

    }

    public void sendMessage(Msg msg) throws SmackException.NotConnectedException, BusinessException {
        sendMessageNoChange(msg);
    }

    public void sendMessage(Message xmppMsg) throws BusinessException, SmackException.NotConnectedException {
        logger.info("xmpp send message:" + JSONUtil.toJson(xmppMsg));
        XMPPConnection connection = this.getXMPPConnectionAuthenticated(xmppMsg.getFrom());
        connection.sendStanza(xmppMsg);
    }

    public AbstractXMPPConnection getInitConnectListenerXmpp(AbstractUser user) throws IOException, XMPPException, SmackException {
        //获取一个xmpp
        AbstractXMPPConnection xmppConnection = this.getXMPPConnectionNew(user);
        xmppConnection.connect();

        return xmppConnection;
    }

    @Override
    public AbstractXMPPConnection getXMPPConnectionNew(AbstractUser user) {
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        //builder.setSendPresence(false);

        XMPPTCPConnectionConfiguration config = builder.setServiceName(Config.xmppdomain).
                setHost(Config.xmppurl).
                //setConnectTimeout()
                        setPort(Integer.valueOf(Config.xmppport)).build();
        AbstractXMPPConnection connection = new XMPPTCPConnection(config);

        connection.setFromMode(org.jivesoftware.smack.XMPPConnection.FromMode.UNCHANGED);

        /*
        设置默认连接策略
         */
        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
        reconnectionManager.enableAutomaticReconnection();
        reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.RANDOM_INCREASING_DELAY);


        ConnectionListener connectionListener = new MUCPacketExtensionProviderAndInitConnectListener(this, user, webSocketService, msgSendControl, doubaoFriendsService);
        //初始化的连接监听器
        connection.addConnectionListener(connectionListener);

        // 设置默认接收所有请求
        Roster roster = Roster.getInstanceFor(connection);
        roster.setRosterLoadedAtLogin(true);
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

        // 添加好友监听器
        UcRosterListener ur = new UcRosterListener(this, msgSendControl, user);
        roster.addRosterListener(ur);


        // 添加群邀请监听器
        final MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

        UcInvitationListener ul = new UcInvitationListener(this, msgService, msgSendControl, user);
        manager.addInvitationListener(ul);


        // 增加消息监听
        ChatManager chatmanager = ChatManager.getInstanceFor(connection);
        ChatManagerListener chatManagerListener = new InitChatManagerListener(user, msgSendControl);
        chatmanager.addChatListener(chatManagerListener);

        //增加自定义会议信息解析
        ProviderManager.addIQProvider("muc", "YANG", connectionListener);
        return connection;
    }

    public boolean login(AbstractUser user) throws SmackException, XMPPException, IOException {
        AbstractXMPPConnection xmppConnection = getInitConnectListenerXmpp(user);

        Ofuser ofuser = ofuserMapper.selectByPrimaryKey(user.getLoginUsername());
        if (null == ofuser) {
            logger.info("创建用户:" + user.getLoginUsername());
            AccountManager accountManager = AccountManager.getInstance(xmppConnection);
            accountManager.createAccount(user.getLoginUsername(), user.getPassWord());
        }

        xmppConnection.login(user.getLoginUsername(), user.getPassWord());
        return true;
    }

    public void roster(String vjid, String cjid) throws BusinessException, SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {
        XMPPConnection createAccountConn = this.getXMPPConnectionAuthenticated(vjid);

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
    public boolean createAccount(String jid, String userName, String password) throws BusinessException, SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        if (isAuthenticated(jid)) {

            AccountManager amgr = AccountManager.getInstance(getXMPPConnectionAuthenticated(jid));
            amgr.createAccount(userName, password);

            return true;
        }
        return false;
    }


    /**
     * 初始化聊服务会议列表
     */
    @Override
    public void getHostRoom(String jid) throws XMPPException, IOException, SmackException, BusinessException {
        if (isAuthenticated(jid)) {

            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(getXMPPConnectionAuthenticated(jid));

            Collection<HostedRoom> hostrooms;

            //manager.getJoinedRooms("");
            Set<String> romms = manager.getJoinedRooms();

            logger.info(romms.toString());

            logger.info(manager.getServiceNames().toString());

        }

    }

    @Override
    public synchronized void joinRoom(MultiUserChat room, AbstractUser user) throws BusinessException, SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        if (!room.isJoined()) {

            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(0);

            room.addParticipantStatusListener(new UcParticipantStatus());
            // 增加 uc 消息监听器
            UcMessageListener messageListener = new UcMessageListener(msgSendControl, user, conversationService, ofmucroomService, msgService);
            room.addMessageListener(messageListener);

            // 用户加入聊天室
            room.join(user.getLoginUsername(), user.getPassWord(), history, this.getXMPPConnection(user.getId()).getPacketReplyTimeout());

            logger.info(user.getLoginUsername() + "加入房间[" + room.getRoom() + "]");
        } else {
            logger.info(user.getLoginUsername() + "已经加入房间");
        }
    }

    @Override
    public void joinRoom(AbstractUser user, Set<String> rooms) throws BusinessException, SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        // 为了性能，延时加载
        if (null != rooms && rooms.size() > 0) {
            for (String room : rooms) {
                joinRoom(user, room);
            }
        }

    }

    @Override
    public void joinRoom(AbstractUser user, String room) throws BusinessException, SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        MultiUserChat muc = getRoom(user, room);

        joinRoom(muc, user);

    }

    @Override
    public MultiUserChat getRoom(AbstractUser user, String room) throws BusinessException, SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        // 为了性能，延时加载
        XMPPConnection conn = this.getXMPPConnection(user.getId());
        final MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(conn);

        //String room = roomsName + "@conference." + conn.getServiceName();
        return manager.getMultiUserChat(room);
    }

}
