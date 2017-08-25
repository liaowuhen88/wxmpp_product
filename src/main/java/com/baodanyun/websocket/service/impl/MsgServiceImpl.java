package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.msg.status.StatusMsg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.model.Ofmucroom;
import com.baodanyun.websocket.service.MessageFiterService;
import com.baodanyun.websocket.service.MsgService;
import com.baodanyun.websocket.service.VcardService;
import com.baodanyun.websocket.service.XmppService;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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

    public static void main(String[] args) {
        String room = "xvql187@conference.126xmpp/\u003cspan class\u003d\"emoji emoji1f4a2\"\u003e\u003c/span\u003e          导演\u003cspan class\u003d\"emoji emojiae\"\u003e\u003c/span\u003e";
        String realRoom = XMPPUtil.removeRoomSource(room);
        System.out.println(realRoom);
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
        return sm;
    }

    @Override
    public ConversationMsg getNewRoomJoines(String room, Ofmucroom ofmucroom, String to) {
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
        filter(sm);
        return sm;
    }

    @Override
    public ConversationMsg getNewPersionalJoines(String realFrom, AbstractUser user, Msg cloneMsg) {

        ConversationMsg sm = new ConversationMsg();
        sm.setKey(realFrom);
        sm.setStatus(StatusMsg.Status.onlineQueueSuccess);
        sm.setType(Msg.Type.status.toString());
        sm.setLoginTime(System.currentTimeMillis());
        sm.setCt(System.currentTimeMillis());
        sm.setTo(user.getId());
        sm.setFromType(Msg.fromType.personal);
        sm.setFrom(realFrom);

        sm.setFromName(realFrom);
        sm.setLoginUsername(realFrom);

        VCard vCard = vcardService.loadVcard(user.getId(), realFrom);

        initByVCard(sm, vCard);

        boolean isEncrypt = messageFiterService.isEncrypt(user.getId(), realFrom);
        if (isEncrypt) {
            sm.setOnlineStatus(ConversationMsg.OnlineStatus.encrypt);
        } else {
            sm.setOnlineStatus(ConversationMsg.OnlineStatus.online);
        }

        filter(sm);
        return sm;
    }

    @Override
    public void initByVCard(ConversationMsg conversationMsg, VCard vCard) {
        logger.info(JSONUtil.toJson(vCard));
        if (null != vCard) {
            conversationMsg.setFromName(vCard.getNickName());
            conversationMsg.setLoginUsername(vCard.getField("FN"));
            byte[] avatar = vCard.getAvatar();
            if (null != avatar) {
                String image = new sun.misc.BASE64Encoder().encode(avatar);
                conversationMsg.setIcon("data:image/jpeg;base64," + image);
            }
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
