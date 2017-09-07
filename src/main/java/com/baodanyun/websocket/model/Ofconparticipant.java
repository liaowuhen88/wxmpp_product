package com.baodanyun.websocket.model;

public class Ofconparticipant {
    private Long conversationid;

    private Long joineddate;

    private Long leftdate;

    private String barejid;

    private String jidresource;

    private String nickname;

    public Long getConversationid() {
        return conversationid;
    }

    public void setConversationid(Long conversationid) {
        this.conversationid = conversationid;
    }

    public Long getJoineddate() {
        return joineddate;
    }

    public void setJoineddate(Long joineddate) {
        this.joineddate = joineddate;
    }

    public Long getLeftdate() {
        return leftdate;
    }

    public void setLeftdate(Long leftdate) {
        this.leftdate = leftdate;
    }

    public String getBarejid() {
        return barejid;
    }

    public void setBarejid(String barejid) {
        this.barejid = barejid == null ? null : barejid.trim();
    }

    public String getJidresource() {
        return jidresource;
    }

    public void setJidresource(String jidresource) {
        this.jidresource = jidresource == null ? null : jidresource.trim();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }
}