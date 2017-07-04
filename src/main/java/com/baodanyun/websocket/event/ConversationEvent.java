package com.baodanyun.websocket.event;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.service.MsgSendControl;

/**
 * Created by liaowuhen on 2017/5/12.
 */
public class ConversationEvent {
    private MsgSendControl msgSendControl;
    private AbstractUser user;
    private Msg cloneMsg;

    public ConversationEvent(AbstractUser user, MsgSendControl msgSendControl, Msg cloneMsg) {
        this.user = user;
        this.msgSendControl = msgSendControl;
        this.cloneMsg = cloneMsg;
    }

    public MsgSendControl getMsgSendControl() {
        return msgSendControl;
    }

    public void setMsgSendControl(MsgSendControl msgSendControl) {
        this.msgSendControl = msgSendControl;
    }

    public AbstractUser getUser() {
        return user;
    }

    public void setUser(AbstractUser user) {
        this.user = user;
    }

    public Msg getCloneMsg() {
        return cloneMsg;
    }

    public void setCloneMsg(Msg cloneMsg) {
        this.cloneMsg = cloneMsg;
    }
}
