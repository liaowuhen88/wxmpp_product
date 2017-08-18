package com.baodanyun.websocket.bean.response;

/**
 * Created by liaowuhen on 2017/7/17.
 */
public class AppKeyResponse {
    private String icon;
    private String nickName;
    private String cName ;
    private String website_icon;
    private String name;
    private String skin;


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getWebsite_icon() {
        return website_icon;
    }

    public void setWebsite_icon(String website_icon) {
        this.website_icon = website_icon;
    }
}
