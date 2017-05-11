package com.baodanyun.websocket.factory;

import com.baodanyun.websocket.core.listener.InitChatManagerListener;
import com.baodanyun.websocket.core.listener.UcInvitationListener;
import com.baodanyun.websocket.core.listener.UcRosterListener;
import com.baodanyun.websocket.util.Config;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

/**
 * Created by liaowuhen on 2017/5/9.
 */
public class XMPPConnectionFactory {
    public static AbstractXMPPConnection getXMPPConnectionNew() {
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        //builder.setSendPresence(false);

        XMPPTCPConnectionConfiguration config = builder.setServiceName(Config.xmppdomain).setHost(Config.xmppurl).setPort(Integer.valueOf(Config.xmppport)).build();
        // XMPPTCPConnectionConfiguration config = builder.setServiceName(Config.xmppdomain).setHost("kefu.tx-network.com").setPort(Integer.valueOf(Config.xmppport)).build();

        AbstractXMPPConnection connection = new XMPPTCPConnection(config);

        connection.setFromMode(org.jivesoftware.smack.XMPPConnection.FromMode.UNCHANGED);

        // 添加群邀请监听器
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

        UcInvitationListener ul = new UcInvitationListener(null,null);
        manager.addInvitationListener(ul);


        /*
        设置默认连接策略
         */
        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
        reconnectionManager.setFixedDelay(10);
        reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.FIXED_DELAY);
        reconnectionManager.enableAutomaticReconnection();

        // 设置默认接收所有请求
        Roster roster = Roster.getInstanceFor(connection);
        roster.setRosterLoadedAtLogin(true);
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);

        // 添加好友监听器
        UcRosterListener ur = new UcRosterListener(null,null);
        roster.addRosterListener(ur);

        // 增加消息监听
        ChatManager chatmanager = ChatManager.getInstanceFor(connection);
        ChatManagerListener chatManagerListener = new InitChatManagerListener(null,null);
        chatmanager.addChatListener(chatManagerListener);

        return connection;
    }
}
