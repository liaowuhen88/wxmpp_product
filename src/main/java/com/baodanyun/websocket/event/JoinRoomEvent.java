package com.baodanyun.websocket.event;

import com.baodanyun.websocket.bean.user.AbstractUser;

import java.util.Set;

/**
 * Created by liaowuhen on 2017/5/12.
 */
public class JoinRoomEvent {
    private AbstractUser user;
    private Set<String> room;

    public JoinRoomEvent(AbstractUser user,Set<String> room){
        this.user = user;
        this.room = room;
    }


    public AbstractUser getUser() {
        return user;
    }

    public void setUser(AbstractUser user) {
        this.user = user;
    }

    public Set<String> getRoom() {
        return room;
    }

    public void setRoom(Set<String> room) {
        this.room = room;
    }
}
