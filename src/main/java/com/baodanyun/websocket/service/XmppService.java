package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.exception.BusinessException;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;

/**
 * Created by liaowuhen on 2016/11/11.
 */

@Service
public interface XmppService {

    /**
     * 是否登录成功
     * @param jid
     * @return
     */
    boolean isAuthenticated(String jid);

    /**
     * 是否已建立连接
     *
     * @param jid
     * @return
     */

    boolean isConnected(String jid) ;

    /**
     * 保存登陆成功的XMppConnection
     *
     * @param jid
     * @param xMPPConnection
     */
    void saveXMPPConnection(String jid, XMPPConnection xMPPConnection) ;

    /**
     * 获取登录成功的XMPPConnection
     *
     * @param jid
     * @return
     */

    XMPPConnection getXMPPConnectionAuthenticated(String jid) throws BusinessException;

    /**
     * 获取成功建立连接的XMPPConnection
     *
     * @param jid
     * @return
     */
    XMPPConnection getXMPPConnection(String jid) ;

    boolean closed(String jid) throws IOException ;


    void sendPresence(String jid, Presence.Type type) throws SmackException.NotConnectedException ;

    void sendMessageNoChange(Msg msg) throws SmackException.NotConnectedException, BusinessException ;

    void sendMessage(Msg msg) throws SmackException.NotConnectedException, BusinessException ;

    /**
     * 获取一个新连接
     * @return
     */
    AbstractXMPPConnection getXMPPConnectionNew(AbstractUser user) ;

    AbstractXMPPConnection getInitConnectListenerXmpp(AbstractUser user) throws IOException, XMPPException, SmackException ;


    boolean login(AbstractUser user) throws SmackException, XMPPException, IOException ;

    void roster(String vjid, String cjid) throws BusinessException, SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException;

    /**
     * 修改密码
     *
     * @return
     */

    boolean changePassword(String jid, String pwd) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException, BusinessException;

    boolean createAccount(String jid,String userName,String password) throws BusinessException, SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException;

    void getHostRoom(String jid) throws XMPPException, IOException, SmackException, BusinessException;

    void joinRoom(MultiUserChat room, AbstractUser user) throws BusinessException, SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException ;

    void joinRoom(AbstractUser user, Set<String> room) throws BusinessException, SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException;

}
