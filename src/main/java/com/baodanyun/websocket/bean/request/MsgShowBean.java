package com.baodanyun.websocket.bean.request;

/**
 * Created by liaowuhen on 2017/8/2.
 */
public class MsgShowBean {
    private String username;
    private String status;
    private Integer count;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
