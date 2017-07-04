package com.baodanyun.websocket.core.listener;

import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.MsgService;
import com.baodanyun.websocket.service.XmppService;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * Created by liaowuhen on 2017/5/10.
 */
public class UcInvitationListener implements InvitationListener {

    private static Logger logger = Logger.getLogger(UcRosterListener.class);

    private MsgSendControl msgSendControl;
    private MsgService msgService;
    private XmppService xmppService;
    private AbstractUser user;


    public UcInvitationListener(XmppService xmppService, MsgService msgService, MsgSendControl msgSendControl, AbstractUser user) {
        this.xmppService = xmppService;
        this.msgSendControl = msgSendControl;
        this.user = user;
        this.msgService = msgService;
    }


    /**
     * // 对应参数：连接、 房间JID、房间名、附带内容、密码、消息
     *
     * @param conn
     * @param room
     * @param inviter
     * @param reason
     * @param password
     * @param message
     */
    @Override
    public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {

        try {
            logger.info("收到来自 " + inviter + " 的聊天" + room.getRoom() + "室邀请。邀请附带内容："
                    + reason);

            xmppService.joinRoom(room,user);

        } catch (Exception e) {
            logger.error(e);
        }
    }

}
