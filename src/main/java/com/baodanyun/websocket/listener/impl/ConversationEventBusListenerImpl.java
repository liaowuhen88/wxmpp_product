package com.baodanyun.websocket.listener.impl;


import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.msg.status.StatusMsg;
import com.baodanyun.websocket.event.ConversationEvent;
import com.baodanyun.websocket.listener.EventBusListener;
import com.baodanyun.websocket.service.XmppService;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import com.google.common.eventbus.Subscribe;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2017/5/12.
 * <p/>
 * 通知客户端有新的会话
 */

@Service
public class ConversationEventBusListenerImpl extends AbstarctEventBusListener<ConversationEvent> implements EventBusListener<ConversationEvent> {

    private static Logger logger = LoggerFactory.getLogger(ConversationEventBusListenerImpl.class);
    @Autowired
    private XmppService xmppService;

    @Override
    @Subscribe
    public boolean processExpiringEvent(final ConversationEvent conversationEvent) {
        logger.info(JSONUtil.toJson(conversationEvent));

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    String from = conversationEvent.getCloneMsg().getFrom();
                    String realFrom = XMPPUtil.removeRoomSource(from);
                    logger.info("from}{}----realFrom{}", from, realFrom);

                    StatusMsg sm = new StatusMsg();
                    sm.setStatus(StatusMsg.Status.onlineQueueSuccess);
                    sm.setType(Msg.Type.status.toString());
                    sm.setLoginTime(System.currentTimeMillis());
                    sm.setCt(System.currentTimeMillis());
                    sm.setTo(conversationEvent.getUser().getId());
                    sm.setFromType(Msg.fromType.personal);
                    sm.setFrom(from);

                    sm.setFromName(from);
                    sm.setLoginUsername(from);

                    VCard vCard = loadVcard(conversationEvent.getUser().getId(), from);
                    logger.info(JSONUtil.toJson(vCard));
                    if (null != vCard) {
                        sm.setFromName(vCard.getFirstName());
                        sm.setLoginUsername(vCard.getNickName());
                        byte[] avatar = vCard.getAvatar();
                        if (null != avatar) {
                            String image = new sun.misc.BASE64Encoder().encode(avatar);
                            sm.setIcon("data:image/jpeg;base64," + image);
                        }
                    }
                    conversationEvent.getMsgSendControl().sendMsg(sm);

                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        });
        return false;
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
