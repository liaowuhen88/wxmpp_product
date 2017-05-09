package com.baodanyun.websocket.model;

import java.util.Date;

public class DoubaoFriends {
    private Integer id;

    private String jid;

    private String jname;

    private String friendJid;

    /**
     * 微信的发送主体
     */
    private String friendJname;

    private String friendIcon;

    private String friendType;

    private String friendGroup;   //群名称或者公众号名称

    private Date ct;

    public String getRealGroupName(){
        return this.getJname()+"_"+this.getFromTypeName();
    }

    public String getFromTypeName(){
        if("11".equals(friendType)){
            return "微信好友";
        }else if("12".equals(friendType)){
            return "微信群";
        }else if("21".equals(friendType)){
            return "公众号";
        }else if("22".equals(friendType)){
            return "22公众号h5";
        }else if("31".equals(friendType)){
            return "QQ好友";
        }else if("32".equals(friendType)){
            return "qq群";
        }else if("33".equals(friendType)){
            return "qq讨论组";
        }else if("41".equals(friendType)){
            return "网页";
        }
        return friendType+"无定义";
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid == null ? null : jid.trim();
    }

    public String getJname() {
        return jname;
    }

    public void setJname(String jname) {
        this.jname = jname == null ? null : jname.trim();
    }

    public String getFriendJid() {
        return friendJid;
    }

    public void setFriendJid(String friendJid) {
        this.friendJid = friendJid;
    }

    public String getFriendJname() {
        return friendJname;
    }

    public void setFriendJname(String friendJname) {
        this.friendJname = friendJname == null ? null : friendJname.trim();
    }

    public String getFriendIcon() {
        return friendIcon;
    }

    public void setFriendIcon(String friendIcon) {
        this.friendIcon = friendIcon == null ? null : friendIcon.trim();
    }

    public String getFriendType() {
        return friendType;
    }

    public void setFriendType(String friendType) {
        this.friendType = friendType == null ? null : friendType.trim();
    }

    public String getFriendGroup() {
        return friendGroup;
    }

    public void setFriendGroup(String friendGroup) {
        this.friendGroup = friendGroup == null ? null : friendGroup.trim();
    }

    public Date getCt() {
        return ct;
    }

    public void setCt(Date ct) {
        this.ct = ct;
    }
}