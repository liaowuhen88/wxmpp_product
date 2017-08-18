package com.baodanyun.websocket.bean.request;

/**
 * Created by liaowuhen on 2017/6/19.
 */
public class AppKeyVisitorLoginBean {
    //LOGIN_USER  登录用户名
    private String appKey;
    private String id;
    private String website_id = "1";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getWebsite_id() {
        return website_id;
    }

    public void setWebsite_id(String website_id) {
        this.website_id = website_id;
    }
}
