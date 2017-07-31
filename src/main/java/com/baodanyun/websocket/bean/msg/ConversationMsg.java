package com.baodanyun.websocket.bean.msg;

import com.baodanyun.websocket.bean.msg.status.StatusMsg;

/**
 * 用于描述消息的状态
 */
public class ConversationMsg extends StatusMsg {
    private OnlineStatus onlineStatus = OnlineStatus.online;
    private String key;
    // 消息是否加密   true 不加密  false 加密
    private Boolean displayStatus = false;

    public ConversationMsg() {
        super();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public OnlineStatus getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(OnlineStatus onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Boolean getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(Boolean displayStatus) {
        this.displayStatus = displayStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        ConversationMsg e = (ConversationMsg) obj;
        return e.getKey().equals(this.getKey());
    }

    @Override
    public int hashCode() {
        return this.getKey().hashCode();
    }

    public enum OnlineStatus {
        online, wait, backup, history
    }
}
