package com.baodanyun.websocket.bean;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by liaowuhen on 2016/12/13.
 */
public class XmppAdapter {
    private XMPPConnection xmpp;
    private Long time;
    private int count; // session 不存在次数

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public XMPPConnection getXmpp() {
        return xmpp;
    }

    public void setXmpp(XMPPConnection xmpp) {
        this.xmpp = xmpp;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
