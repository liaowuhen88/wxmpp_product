package com.baodanyun.websocket.bean.bootstrap;

import java.util.List;

/**
 * Created by liaowuhen on 2017/8/31.
 */
public class Node {
    private String text;
    private String icon;
    private String jid;

    private List<Node> nodes;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }
}
