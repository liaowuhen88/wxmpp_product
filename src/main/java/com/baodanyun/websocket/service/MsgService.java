package com.baodanyun.websocket.service;


import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.Ofmucroom;
import com.baodanyun.websocket.model.Ofvcard;

/**
 * Created by liaowuhen on 2017/5/11.
 */
public interface MsgService {
    ConversationMsg getNewWebJoines(AbstractUser user, String to);

    ConversationMsg getNewRoomJoines(String room, Ofmucroom ofmucroom, String to, String appKey);

    ConversationMsg getNewPersionalJoines(String realFrom, AbstractUser user) throws BusinessException;

    void initByVCard(ConversationMsg conversationMsg, Ofvcard vCard);

    void filter(ConversationMsg conversationMsg);
}
