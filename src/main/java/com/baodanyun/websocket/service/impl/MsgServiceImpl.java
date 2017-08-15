package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.msg.status.StatusMsg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.model.Ofmucroom;
import com.baodanyun.websocket.service.MessageFiterService;
import com.baodanyun.websocket.service.MsgService;
import com.baodanyun.websocket.service.XmppService;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
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
    private MessageFiterService messageFiterService;

    public static void main(String[] args) {
        String room = "xvql187@conference.126xmpp/\u003cspan class\u003d\"emoji emoji1f4a2\"\u003e\u003c/span\u003e          导演\u003cspan class\u003d\"emoji emojiae\"\u003e\u003c/span\u003e";
        String realRoom = XMPPUtil.removeRoomSource(room);
        System.out.println(realRoom);
    }

    @Override
    public ConversationMsg getNewWebJoines(AbstractUser user, String to) {
        String realJid = XMPPUtil.removeRoomSource(user.getId());
        ConversationMsg sm = new ConversationMsg();
        sm.setKey(realJid);
        sm.setStatus(StatusMsg.Status.onlineQueueSuccess);
        sm.setType(Msg.Type.status.toString());
        sm.setLoginTime(new Date().getTime());
        sm.setIcon("data:image/png;base64,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");
        //
        sm.setFromType(Msg.fromType.personal);
        sm.setFrom(realJid);
        sm.setTo(to);
        sm.setCt(new Date().getTime());

        sm.setFromName("访客(" + user.getLoginUsername() + ")");
        sm.setLoginUsername(user.getNickName());

        boolean isEncrypt = messageFiterService.isEncrypt(to, realJid);
        if (isEncrypt) {
            sm.setOnlineStatus(ConversationMsg.OnlineStatus.encrypt);
        } else {
            sm.setOnlineStatus(ConversationMsg.OnlineStatus.online);
        }

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

        VCard vCard = loadVcard(user.getId(), realFrom);
        logger.info(JSONUtil.toJson(vCard));
        if (null != vCard) {
            sm.setFromName(vCard.getNickName());
            sm.setLoginUsername(vCard.getField("FN"));
            byte[] avatar = vCard.getAvatar();
            if (null != avatar) {
                String image = new sun.misc.BASE64Encoder().encode(avatar);
                sm.setIcon("data:image/jpeg;base64," + image);
            }
        }

        boolean isEncrypt = messageFiterService.isEncrypt(user.getId(), realFrom);
        if (isEncrypt) {
            sm.setOnlineStatus(ConversationMsg.OnlineStatus.encrypt);
        } else {
            sm.setOnlineStatus(ConversationMsg.OnlineStatus.online);
        }
        return sm;
    }

    public VCard loadVcard(String xmppid, String Jid) {
        VCard vcard = null;
        try {
            vcard = VCardManager.getInstanceFor(xmppService.getXMPPConnectionAuthenticated(xmppid)).loadVCard(Jid);
        } catch (Exception e) {
            logger.error("", e);
        }
        return vcard;
    }

}
