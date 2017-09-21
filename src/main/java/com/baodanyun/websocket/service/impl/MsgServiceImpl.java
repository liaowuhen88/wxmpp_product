package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.msg.status.StatusMsg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.GroupUser;
import com.baodanyun.websocket.bean.user.PublicUser;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.Ofmucroom;
import com.baodanyun.websocket.model.Ofvcard;
import com.baodanyun.websocket.service.*;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by liaowuhen on 2017/5/11.
 */

@Service
public class MsgServiceImpl implements MsgService {

    protected static Logger logger = LoggerFactory.getLogger(MsgServiceImpl.class);
    @Autowired
    private XmppService xmppService;
    @Autowired
    private VcardService vcardService;

    @Autowired
    private MessageFiterService messageFiterService;

    @Autowired
    private ConversationService conversationService;
    @Autowired
    private FriendAndGroupService friendAndGroupService;

    public static void main(String[] args) {
        String room = "xvql187@conference.126xmpp/\u003cspan class\u003d\"emoji emoji1f4a2\"\u003e\u003c/span\u003e          导演\u003cspan class\u003d\"emoji emojiae\"\u003e\u003c/span\u003e";
        String realRoom = XMPPUtil.removeRoomSource(room);
        System.out.println(realRoom);
    }

    @Override
    public ConversationMsg getNewPublic(PublicUser pu, String to) {
        ConversationMsg sm = new ConversationMsg();
        sm.setKey(pu.getRealFrom());
        sm.setStatus(StatusMsg.Status.onlineQueueSuccess);
        sm.setType(Msg.Type.status.toString());
        sm.setLoginTime(System.currentTimeMillis());
        sm.setCt(System.currentTimeMillis());
        sm.setTo(to);
        sm.setFromType(Msg.fromType.publicSignal);
        sm.setFrom(pu.getRealFrom());

        sm.setFromName(pu.getNickname());
        sm.setLoginUsername("公众号[" + pu.getPublic_name() + "]");
        sm.setIcon(pu.getIcon());
        boolean isEncrypt = messageFiterService.isEncrypt(pu.getUid(), pu.getRealFrom());
        if (isEncrypt) {
            sm.setOnlineStatus(ConversationMsg.OnlineStatus.encrypt);
        } else {
            sm.setOnlineStatus(ConversationMsg.OnlineStatus.online);
        }

        filter(sm);

        conversationService.addConversations(pu.getUid(), sm);
        return sm;
    }

    @Override
    public ConversationMsg getNewWebJoines(AbstractUser user, String to) {
        String realJid = user.getAgencyFrom();
        ConversationMsg sm = new ConversationMsg();
        sm.setKey(realJid);
        sm.setStatus(StatusMsg.Status.onlineQueueSuccess);
        sm.setType(Msg.Type.status.toString());
        sm.setLoginTime(new Date().getTime());
        sm.setIcon(user.getIcon());
        //
        sm.setFromType(Msg.fromType.personal);
        sm.setFrom(user.getAgencyFrom());
        sm.setTo(to);
        sm.setCt(new Date().getTime());

        sm.setFromName("访客(" + user.getUserName() + ")");
        sm.setLoginUsername(user.getNickName());

        boolean isEncrypt = messageFiterService.isEncrypt(to, realJid);
        if (isEncrypt) {
            sm.setOnlineStatus(ConversationMsg.OnlineStatus.encrypt);
        } else {
            sm.setOnlineStatus(ConversationMsg.OnlineStatus.online);
        }
        filter(sm);

        conversationService.addConversations(to, sm);
        return sm;
    }

    /**
     * 当前需要加入群的用户
     *
     * @param room
     * @param ofmucroom
     * @param to
     * @param appKey
     * @param user
     * @return
     * @throws XMPPException.XMPPErrorException
     * @throws SmackException.NoResponseException
     * @throws SmackException.NotConnectedException
     * @throws BusinessException
     */
    @Override
    public ConversationMsg getNewRoomJoines(String room, Ofmucroom ofmucroom, String to, String appKey, AbstractUser user) throws XMPPException.XMPPErrorException, SmackException.NoResponseException, SmackException.NotConnectedException, BusinessException {
        String realRoom = XMPPUtil.removeRoomSource(room);
        ConversationMsg sm = new ConversationMsg();
        sm.setKey(realRoom);
        sm.setStatus(StatusMsg.Status.onlineQueueSuccess);
        sm.setType(Msg.Type.status.toString());
        sm.setLoginTime(new Date().getTime());
        //
        sm.setIcon("http://vipkefu.oss-cn-shanghai.aliyuncs.com/vvZhuShou/" + XMPPUtil.jidToName(realRoom) + ".jpg");
        sm.setFromType(Msg.fromType.group);
        sm.setFrom(realRoom);
        sm.setTo(to);
        sm.setCt(new Date().getTime());

        if (null != ofmucroom) {
            sm.setFromName(ofmucroom.getNaturalname());
            sm.setLoginUsername(ofmucroom.getDescription());
        }
        boolean isEncrypt = messageFiterService.isEncrypt(to, realRoom);
        if (isEncrypt) {
            sm.setOnlineStatus(ConversationMsg.OnlineStatus.encrypt);
        } else {
            sm.setOnlineStatus(ConversationMsg.OnlineStatus.online);
        }

        // 初始化群成员
        try {
            if (StringUtils.isNotEmpty(appKey)) {
                List<GroupUser> list = friendAndGroupService.getGroupUsers(appKey, XMPPUtil.jidToName(realRoom));
                if (null != list) {
                    for (GroupUser gu : list) {
                        sm.getGroupUserMap().put(XMPPUtil.jidToName(gu.getJid()), gu);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("error", e);
        }

        filter(sm);
        conversationService.addConversations(to, sm);
        return sm;
    }

    @Override
    public ConversationMsg getNewPersionalJoines(String realFrom, AbstractUser user) {

        ConversationMsg sm = new ConversationMsg();
        sm.setKey(realFrom);
        sm.setStatus(StatusMsg.Status.onlineQueueSuccess);
        sm.setType(Msg.Type.status.toString());
        sm.setLoginTime(System.currentTimeMillis());
        sm.setCt(System.currentTimeMillis());
        sm.setTo(user.getId());
        sm.setFromType(Msg.fromType.personal);
        sm.setFrom(realFrom);

        //sm.setFromName(realFrom);
        //sm.setLoginUsername(realFrom);

        Ofvcard vCard = null;
        try {
            vCard = vcardService.loadVcard(XMPPUtil.jidToName(realFrom));
        } catch (BusinessException e) {
            logger.error("error", e);
        }

        initByVCard(sm, vCard);

        boolean isEncrypt = messageFiterService.isEncrypt(user.getId(), realFrom);
        if (isEncrypt) {
            sm.setOnlineStatus(ConversationMsg.OnlineStatus.encrypt);
        } else {
            sm.setOnlineStatus(ConversationMsg.OnlineStatus.online);
        }

        filter(sm);

        conversationService.addConversations(user.getId(), sm);

        return sm;
    }

    @Override
    public void initByVCard(ConversationMsg conversationMsg, Ofvcard vCard) {
        if (null != vCard) {
            //logger.info(JSONUtil.toJson(vCard));
            conversationMsg.setFromName(vCard.getUser().getNickName());
            conversationMsg.setLoginUsername(vCard.getUser().getFn());
            //byte[] avatar = vCard.getUser().getAvatar();
            String avatar = vCard.getUser().getAvatar();
            if (null != avatar) {
                //String image = new sun.misc.BASE64Encoder().encode(avatar);
                conversationMsg.setIcon("data:image/jpeg;base64," + avatar);
            }
        } else {
            logger.info("vCard is null");
        }
    }

    @Override
    public void filter(ConversationMsg conversationMsg) {
        if (StringUtils.isEmpty(conversationMsg.getFromName())) {
            conversationMsg.setFromName("未设置");
        }
        if (StringUtils.isEmpty(conversationMsg.getLoginUsername())) {
            conversationMsg.setLoginUsername("未设置");
        }
    }


}
