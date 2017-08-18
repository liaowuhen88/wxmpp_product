package com.baodanyun.websocket.service;


import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.model.Ofmucroom;

/**
 * Created by liaowuhen on 2017/5/11.
 */
public interface MsgService {
    ConversationMsg getNewWebJoines(AbstractUser user, String to);

    ConversationMsg getNewRoomJoines(String room, Ofmucroom ofmucroom, String to);

    ConversationMsg getNewPersionalJoines(String realFrom, AbstractUser user, Msg cloneMsg);

    void filter(ConversationMsg conversationMsg);
}
