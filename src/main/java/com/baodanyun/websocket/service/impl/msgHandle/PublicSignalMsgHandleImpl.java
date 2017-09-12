package com.baodanyun.websocket.service.impl.msgHandle;

import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.PublicUser;
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
@Service("psMsgHandle")
public class PublicSignalMsgHandleImpl extends AbstractMsgHandleService {
    protected static Logger logger = LoggerFactory.getLogger(PublicSignalMsgHandleImpl.class);
    @Autowired
    private FriendAndGroupService friendAndGroupService;

    @Override
    public boolean canHandel(Message msg, Msg sendMsg) {
        //from:system@126xmpp/__public_1@126xmpp

        String from = msg.getFrom();
        String[] ffs = from.split("__");
        if (null != ffs && ffs.length > 1 && ffs[0].startsWith("system") && ffs[1].startsWith("public")) {
            return true;
        }

        return false;
    }

    /**
     * 取带资源地址
     *
     * @param msg
     * @param sendMsg
     */
    @Override
    public void handel(AbstractUser user, Message msg, Msg sendMsg) {
        try {
            String from = msg.getFrom();
            sendMsg.setFrom(from);
            ConversationMsg conversation = initConversationMsg(user, sendMsg);
            sendMsg.setFromName(conversation.getFromName());
            sendMsg.setIcon(conversation.getIcon());
        } catch (Exception e) {
            logger.error("error", e);
        }
    }

    @Override
    public ConversationMsg initConversationMsg(AbstractUser user, Msg sendMsg) {
        ConversationMsg conversation = initByCache(user, sendMsg);
        if (null != conversation) {
            logger.info(" user {}, realFrom {} isExist", user.getId(), sendMsg.getFrom());
        } else {
            String from = sendMsg.getFrom();
            String[] ffs = from.split("__");
            logger.info(" from {}, ffs[1] {} isNotExist", from, ffs[1]);
            try {
                PublicUser pu = friendAndGroupService.getPublicUser(user.getAppkey(), XMPPUtil.jidToName(ffs[1]));
                pu.setRealFrom(from);
                pu.setUid(user.getId());
                conversation = msgService.getNewPublic(pu, sendMsg.getTo());
            } catch (Exception e) {
                logger.error("error", e);
            }

        }

        return conversation;
    }
}
