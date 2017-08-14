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
