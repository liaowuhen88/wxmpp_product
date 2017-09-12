package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by liaowuhen on 2017/9/12.
 */
public interface MsgHandleService {
    boolean canHandel(Message msg, Msg sendMsg);

    void handel(AbstractUser user, Message msg, Msg sendMsg);

    ConversationMsg initConversationMsg(AbstractUser user, Msg sendMsg);

    ConversationMsg initByCache(AbstractUser user, Msg sendMsg);

}
