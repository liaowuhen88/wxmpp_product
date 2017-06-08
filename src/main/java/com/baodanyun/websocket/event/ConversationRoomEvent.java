package com.baodanyun.websocket.event;

import com.baodanyun.websocket.bean.user.AbstractUser;

/**
 * Created by liaowuhen on 2017/5/12.
 */
public class ConversationRoomEvent {
    private AbstractUser user;
    private String room;

    public ConversationRoomEvent(AbstractUser user, String room) {
        this.user = user;
        this.room = room;
    }


    public AbstractUser getUser() {
        return user;
    }

    public void setUser(AbstractUser user) {
        this.user = user;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
