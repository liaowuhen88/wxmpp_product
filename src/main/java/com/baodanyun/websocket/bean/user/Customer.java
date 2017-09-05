package com.baodanyun.websocket.bean.user;

import java.io.Serializable;

/**
 * Created by yutao on 2016/7/12.
 * 客服应该是有别于其他用户的
 */
public class Customer extends AbstractUser implements Serializable {

    private String appkey;

    public Customer() {
        setUserType(UserType.customer);
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }
}
