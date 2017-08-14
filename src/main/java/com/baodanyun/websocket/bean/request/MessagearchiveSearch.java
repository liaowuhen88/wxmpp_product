package com.baodanyun.websocket.bean.request;

/**
 * Created by liaowuhen on 2017/7/1.
 */
public class MessagearchiveSearch {
    private String fromJid;
    private String toJid;

    public String getFromJid() {
        return fromJid;
    }

    public void setFromJid(String fromJid) {
        this.fromJid = fromJid;
    }

    public String getToJid() {
        return toJid;
    }

    public void setToJid(String toJid) {
        this.toJid = toJid;
    }
}
