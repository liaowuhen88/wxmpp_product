package com.baodanyun.websocket.factory;

import com.baodanyun.websocket.util.Config;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

/**
 * Created by liaowuhen on 2017/5/9.
 */
public class XMPPConnectionFactory {
    public static AbstractXMPPConnection getXMPPConnectionNew() {
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        //builder.setSendPresence(false);

        XMPPTCPConnectionConfiguration config = builder.setServiceName(Config.xmppdomain).setHost(Config.xmppurl).setPort(Integer.valueOf(Config.xmppport)).build();
        AbstractXMPPConnection connection = new XMPPTCPConnection(config);

        connection.setFromMode(org.jivesoftware.smack.XMPPConnection.FromMode.UNCHANGED);

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

        return connection;
    }
}
