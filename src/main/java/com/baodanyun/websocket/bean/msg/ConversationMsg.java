package com.baodanyun.websocket.bean.msg;

import com.baodanyun.websocket.bean.msg.status.StatusMsg;
import com.baodanyun.websocket.bean.user.GroupUser;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于描述消息的状态
 */
public class ConversationMsg extends StatusMsg {
    private Map<String,GroupUser> groupUserMap = new HashMap<>();
    private OnlineStatus onlineStatus = OnlineStatus.online;
    private String key;
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

    public Map<String, GroupUser> getGroupUserMap() {
        return groupUserMap;
    }

    public enum OnlineStatus {
        online, wait, backup, history
    }

}
