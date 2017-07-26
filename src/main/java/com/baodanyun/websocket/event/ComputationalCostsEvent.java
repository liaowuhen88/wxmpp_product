package com.baodanyun.websocket.event;

import com.baodanyun.websocket.bean.msg.Msg;

/**
 * Created by liaowuhen on 2017/5/12.
 */
public class ComputationalCostsEvent {
    private String jid;
    private Msg cloneMsg;

    public ComputationalCostsEvent(String jid, Msg cloneMsg) {
        this.jid = jid;
        this.cloneMsg = cloneMsg;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public Msg getCloneMsg() {
        return cloneMsg;
    }

    public void setCloneMsg(Msg cloneMsg) {
        this.cloneMsg = cloneMsg;
    }
}
