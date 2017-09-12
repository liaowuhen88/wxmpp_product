package com.baodanyun.websocket.service.impl.msgHandle;

import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import org.jivesoftware.smack.packet.Message;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2017/9/12.
 * <p/>
 * 用户发送到客服的消息处理类
 */
@Service("h5ToCMsgHandle")
public class H5ToCustomerMsgHandleImpl extends AbstractMsgHandleService {
    @Override
    public boolean canHandel(Message msg, Msg sendMsg) {
        String from = msg.getFrom();
        //to":"xvsh7@126xmpp","from":"xvsh7-web_3903276471@126xmpp"
        if (from.contains("-web_")) {
            return true;
        }
        return false;
    }

    /**
     * 修改to地址发送到指定的web接入用户
     *
     * @param user
     * @param msg
     * @param sendMsg "to":"xvsh7@126xmpp","from":"xvsh7-web_3903276471@126xmpp"
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

        return conversation;
    }
}
