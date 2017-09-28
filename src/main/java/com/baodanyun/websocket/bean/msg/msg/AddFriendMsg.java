package com.baodanyun.websocket.bean.msg.msg;

import com.baodanyun.websocket.bean.msg.Msg;

/**
 * Created by yutao on 2016/7/12.
 */
public class AddFriendMsg extends Msg {
    private String friend;

    public AddFriendMsg(String content) {
        setContentType(MsgContentType.addfriend.toString());
        setContent(content);
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }
}
