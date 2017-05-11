package com.baodanyun.websocket.core.listener;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.msg.status.StatusMsg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.service.MsgSendControl;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;
import java.util.Date;

/**
 * Created by liaowuhen on 2017/5/10.
 */


public class UcRosterListener implements RosterListener {
    private static Logger logger= Logger.getLogger(UcRosterListener.class);
    private MsgSendControl msgSendControl;
    private AbstractUser user;

    public UcRosterListener(MsgSendControl msgSendControl,AbstractUser user){
        this.msgSendControl = msgSendControl;
        this.user = user;
    }

    public void entriesAdded(Collection<String> addresses) {
        try {
            logger.info("添加新的好友："+addresses);
            if(null != addresses && addresses.size() > 0 ){
                for(String address:addresses){
                    StatusMsg sm = new  StatusMsg();
                    sm.setStatus(StatusMsg.Status.onlineQueueSuccess);
                    sm.setType(Msg.Type.status.toString());
                    sm.setLoginTime(new Date().getTime());
                    sm.setFromName(address);
                    sm.setFrom(address);
                    sm.setLoginUsername(address);
                    sm.setTo(user.getId());
                    sm.setCt(new Date().getTime());

                    msgSendControl.sendMsg(sm);
                }
            }

        } catch (InterruptedException e) {
            logger.equals(e);
        } catch (Exception e) {
            logger.equals(e);
        }

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