package com.baodanyun.websocket.model;

public class OfMessagearchiveWithBLOBs extends OfMessagearchive {
    private String stanza;

    private String body;

    public String getStanza() {
        return stanza;
    }

    public void setStanza(String stanza) {
        this.stanza = stanza == null ? null : stanza.trim();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body == null ? null : body.trim();
    }
}