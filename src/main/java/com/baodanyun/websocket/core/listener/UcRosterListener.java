package com.baodanyun.websocket.core.listener;

import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.XmppService;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.Collection;

/**
 * Created by liaowuhen on 2017/5/10.
 */


public class UcRosterListener implements RosterListener {
    private static Logger logger= Logger.getLogger(UcRosterListener.class);
    private MsgSendControl msgSendControl;
    private XmppService xmppService;
    private AbstractUser user;

    public UcRosterListener(XmppService xmppService,MsgSendControl msgSendControl,AbstractUser user){
        this.xmppService = xmppService;
        this.msgSendControl = msgSendControl;
        this.user = user;
    }

    public void entriesAdded(Collection<String> addresses) {
        try {
            logger.info("添加新的好友："+addresses);
            /*if(null != addresses && addresses.size() > 0 ){
                for(String address:addresses){
                    StatusMsg sm = new  StatusMsg();
                    sm.setStatus(StatusMsg.Status.onlineQueueSuccess);
                    sm.setType(Msg.Type.status.toString());
                    sm.setLoginTime(System.currentTimeMillis());
                    sm.setCt(System.currentTimeMillis());
                    sm.setTo(user.getId());
                    sm.setFromType(Msg.fromType.personal);
                    sm.setFrom(address);

                    sm.setFromName(address);
                    sm.setLoginUsername(address);

                   VCard vCard = loadVcard(address);
                    if(null != vCard){
                        sm.setFromName(vCard.getFirstName());
                        sm.setLoginUsername(vCard.getNickName());
                    }
                    msgSendControl.sendMsg(sm);
                }
            }*/

        } catch (Exception e) {
            logger.error(e);
        }

    }

    public VCard loadVcard(String Jid){
        VCard vcard = null;
        try {
             vcard  = VCardManager.getInstanceFor(xmppService.getXMPPConnectionAuthenticated(user.getId())).loadVCard(Jid);
        } catch (Exception e) {
            logger.error(e);
        }
        return vcard;
    }

    public void entriesDeleted(Collection<String> addresses) {
        logger.info("删除的好友："+addresses);
    }

    public void entriesUpdated(Collection<String> addresses) {
        logger.info("变化的好友："+addresses);
    }

    public void presenceChanged(Presence presence) {
        String from  = presence.getFrom();
        String to  =presence.getTo();
        String status = presence.getStatus();
        Presence.Type type = presence.getType();
        logger.info("好友状态变化Presence changed:"+from+":"+status+":"+type+",to:"+to);
    }

}