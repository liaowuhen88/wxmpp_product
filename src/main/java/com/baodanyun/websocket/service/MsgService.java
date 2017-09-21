package com.baodanyun.websocket.service;


import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.PublicUser;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.Ofmucroom;
import com.baodanyun.websocket.model.Ofvcard;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by liaowuhen on 2017/5/11.
 */
public interface MsgService {
    /**
     * 获取公账号
     *
     * @param pu
     * @param to
     * @return
     */
    ConversationMsg getNewPublic(PublicUser pu, String to);

    ConversationMsg getNewWebJoines(AbstractUser user, String to);

    ConversationMsg getNewRoomJoines(String room, Ofmucroom ofmucroom, String to, String appKey, AbstractUser user) throws XMPPException.XMPPErrorException, SmackException.NoResponseException, SmackException.NotConnectedException, BusinessException;

    ConversationMsg getNewPersionalJoines(String realFrom, AbstractUser user) throws BusinessException;

    void initByVCard(ConversationMsg conversationMsg, Ofvcard vCard);

    void filter(ConversationMsg conversationMsg);
}
