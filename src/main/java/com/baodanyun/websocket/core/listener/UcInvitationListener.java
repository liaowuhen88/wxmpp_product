package com.baodanyun.websocket.core.listener;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.MsgService;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * Created by liaowuhen on 2017/5/10.
 */
public class UcInvitationListener implements InvitationListener {

    private static Logger logger = Logger.getLogger(UcRosterListener.class);

    private MsgSendControl msgSendControl;
    private MsgService msgService;

    private AbstractUser user;


    public UcInvitationListener(MsgService msgService,MsgSendControl msgSendControl,AbstractUser user){
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
            logger.info("收到来自 " + inviter + " 的聊天"+room.getRoom()+"室邀请。邀请附带内容："
                    + reason);

            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(0);

            if(!room.isJoined()){
                // 用户加入聊天室
                room.join(user.getLoginUsername(), password, history, conn.getPacketReplyTimeout());
                // 增加 uc 消息监听器
                MessageListener messageListener = new UcMessageListener(msgSendControl,user);
                room.addMessageListener(messageListener);

            }

            sengNewRoom(room,inviter);


        } catch (InterruptedException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
    }


    public void sengNewRoom(MultiUserChat room,String inviter) throws InterruptedException {
        Msg msg = msgService.getNewRoomJoines(room.getRoom(),user.getId());

        msgSendControl.sendMsg(msg);
    }
}
