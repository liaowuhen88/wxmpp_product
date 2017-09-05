package com.baodanyun.websocket.bean.user;

/**
 * Created by liaowuhen on 2017/9/5.
 */
public class GroupUser {
    private String icon;
    private String nickname;
    private String jid;
    private String is_friend;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getIs_friend() {
        return is_friend;
    }

    public void setIs_friend(String is_friend) {
        this.is_friend = is_friend;
    }
}
