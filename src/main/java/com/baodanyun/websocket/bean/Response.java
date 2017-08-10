package com.baodanyun.websocket.bean;

import org.apache.commons.lang.StringUtils;

/**
 * Created by yutao on 2016/7/13.
 */
public class Response {
    private boolean success;
    private int code;
    private String msg;
    private String message;
    private Object data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        if (StringUtils.isNotEmpty(msg)) {
            return msg;
        }
        return message;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
