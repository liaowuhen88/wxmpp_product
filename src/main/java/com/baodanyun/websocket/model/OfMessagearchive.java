package com.baodanyun.websocket.model;

public class OfMessagearchive {
    private Long messageid;

    private Long conversationid;

    private String fromjid;

    private String fromjidresource;

    private String tojid;

    private String tojidresource;

    private Long sentdate;

    public Long getMessageid() {
        return messageid;
    }

    public void setMessageid(Long messageid) {
        this.messageid = messageid;
    }

    public Long getConversationid() {
        return conversationid;
    }

    public void setConversationid(Long conversationid) {
        this.conversationid = conversationid;
    }

    public String getFromjid() {
        return fromjid;
    }

    public void setFromjid(String fromjid) {
        this.fromjid = fromjid == null ? null : fromjid.trim();
    }

    public String getFromjidresource() {
        return fromjidresource;
    }

    public void setFromjidresource(String fromjidresource) {
        this.fromjidresource = fromjidresource == null ? null : fromjidresource.trim();
    }

    public String getTojid() {
        return tojid;
    }

    public void setTojid(String tojid) {
        this.tojid = tojid == null ? null : tojid.trim();
    }

    public String getTojidresource() {
        return tojidresource;
    }

    public void setTojidresource(String tojidresource) {
        this.tojidresource = tojidresource == null ? null : tojidresource.trim();
    }

    public Long getSentdate() {
        return sentdate;
    }

    public void setSentdate(Long sentdate) {
        this.sentdate = sentdate;
    }
}