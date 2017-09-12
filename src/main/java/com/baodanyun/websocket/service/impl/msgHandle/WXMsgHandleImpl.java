package com.baodanyun.websocket.service.impl.msgHandle;

import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.Ofmucroom;
import com.baodanyun.websocket.service.FriendAndGroupService;
import com.baodanyun.websocket.util.XMPPUtil;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2017/9/12.
 */
@Service("wxMsgHandle")
public class WXMsgHandleImpl extends AbstractMsgHandleService {
    protected static Logger logger = LoggerFactory.getLogger(WXMsgHandleImpl.class);
    @Autowired
    private FriendAndGroupService friendAndGroupService;

    @Override
    public boolean canHandel(Message msg, Msg sendMsg) {
        //from:system@126xmpp/__public_1@126xmpp
        return true;
    }

    /**
     * @param msg
     * @param sendMsg
     */
    @Override
    public void handel(AbstractUser user, Message msg, Msg sendMsg) {

        ConversationMsg conversation = initConversationMsg(user, sendMsg);

        if (null != conversation) {
            sendMsg.setFromName(conversation.getFromName());
            sendMsg.setIcon(conversation.getIcon());
        }

    }

    @Override
    public ConversationMsg initConversationMsg(AbstractUser user, Msg sendMsg) {
        ConversationMsg conversation = initByCache(user, sendMsg);
        if (null != conversation) {
            logger.info(" user {}, realFrom {} isExist", user.getId(), sendMsg.getFrom());
        } else {
            logger.info(" user {}, realFrom {} notExist", user.getId(), sendMsg.getFrom());
            // 为了解决系统发送的群消息
            if (XMPPUtil.isRoom(sendMsg.getFrom())) {
                Ofmucroom ofmucroom;
                try {
                    ofmucroom = ofmucroomService.selectByPrimaryKey((long) 1, XMPPUtil.getRoomName(sendMsg.getFrom()));
                    conversation = msgService.getNewRoomJoines(sendMsg.getFrom(), ofmucroom, user.getId(), user.getAppkey());

                } catch (BusinessException e) {
                    logger.error("error", e);
                }
            } else {
                try {
                    conversation = msgService.getNewPersionalJoines(sendMsg.getFrom(), user);
                } catch (BusinessException e) {
                    logger.error("error", e);
                }
            }
            //msgSendControl.sendMsg(conversation);
        }

        return conversation;
    }
}
