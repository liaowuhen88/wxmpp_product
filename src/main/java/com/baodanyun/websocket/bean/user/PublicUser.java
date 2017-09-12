package com.baodanyun.websocket.bean.user;

/**
 * Created by liaowuhen on 2017/9/12.
 */
public class PublicUser extends CommonUser {
    private String public_name;

    private String uid;

    private String realFrom;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRealFrom() {
        return realFrom;
    }

    public void setRealFrom(String realFrom) {
        this.realFrom = realFrom;
    }

    public String getPublic_name() {
        return public_name;
    }

    public void setPublic_name(String public_name) {
        this.public_name = public_name;
    }
}
