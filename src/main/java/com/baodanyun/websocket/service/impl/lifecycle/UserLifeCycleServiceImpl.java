package com.baodanyun.websocket.service.impl.lifecycle;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.msg.msg.ImgMsg;
import com.baodanyun.websocket.bean.msg.msg.TextMsg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.listener.VisitorListener;
import com.baodanyun.websocket.service.*;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by liaowuhen on 2017/3/6.
 */
public abstract class UserLifeCycleServiceImpl implements UserLifeCycleService {
    public static final Logger logger = LoggerFactory.getLogger(UserLifeCycleServiceImpl.class);
    @Autowired
    public LastVisitorSendMessageService lastVisitorSendMessageService;
    @Autowired
    public VisitorListener visitorListener;
    @Autowired
    protected XmppService xmppService;
    @Autowired
    protected VcardService vcardService;
    @Autowired
    protected UserCacheServer userCacheServer;

    @Autowired
    protected FriendAndGroupService friendAndGroupService;


    @Override
    public boolean login(AbstractUser user) throws IOException, XMPPException, SmackException, BusinessException, InterruptedException {
        if (xmppService.isAuthenticated(user.getId())) {
            return true;
        }
       return xmppService.login(user);
    }

    protected boolean init(AbstractUser user) throws InterruptedException, SmackException.NotLoggedInException, SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, BusinessException {
        if (xmppService.isAuthenticated(user.getId())) {
            try {
                xmppService.sendPresence(user.getId(), Presence.Type.available);
                return true;
            } catch (Exception e) {
                logger.error(" xmppConnection.sendStanza(presence);", e);
                return false;
            }
        }
        return false ;
    }

    @Override
    public boolean online(AbstractUser user) throws InterruptedException, BusinessException, SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {
        init(user);
        pushOfflineMsg(user);

        return false;
    }


    public boolean pushOfflineMsg(AbstractUser user) throws BusinessException {
        //加载离线记录
        XMPPConnection xmppConnection = xmppService.getXMPPConnectionAuthenticated(user.getId());
        OfflineMessageManager offlineManager = new OfflineMessageManager(xmppConnection);
        try {
            List<Message> msgList = offlineManager.getMessages();
            if (!CollectionUtils.isEmpty(msgList)) {
                for (Message message : msgList) {
                    String body = message.getBody();
                    if (StringUtils.isNotBlank(body)) {
                        Msg msgBean = Msg.handelMsg(body);
                        if (msgBean != null) {
                            //只接收文本,图片类型消息
                            if (msgBean instanceof TextMsg || msgBean instanceof ImgMsg) {
                                getMsgSendService().produce(msgBean);
                                //只有接受到消息后 才删除离线消息
                                offlineManager.deleteMessages();

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("offline msg error", e);
        }

        return true;
    }

    @Override
    public void logout(AbstractUser user) throws InterruptedException {
        try {
            xmppService.closed(user.getId());
        } catch (IOException e) {
            logger.info("closed xmpp error", e);
        }
    }

    @Override
    public Msg receiveMessage(AbstractUser user, String content) throws Exception {
        Msg msg = getMsg(user,content);
        if(null != msg){
            // TODO
            if (XMPPUtil.isRoom(msg.getTo())) {
                // 加入群
                String realRoom = XMPPUtil.removeRoomSource(msg.getTo());
                MultiUserChat muc = xmppService.getRoom(user.getId(), realRoom);
                if (null == muc) {
                    logger.error("room [{}] is null  ", realRoom);
                } else {
                    xmppService.joinRoom(muc, user);
                }
                String to = friendAndGroupService.getRobotJid(user.getAppkey(), XMPPUtil.jidToName(realRoom));
                logger.info(to);
                muc.invite(to, "插件入群");
                //mc.getMembers()
            }
            xmppService.sendMessage(msg );
        }
        return msg;
    }

    public abstract Msg getMsg(AbstractUser user, String content);

    @Override
    public boolean uninstallVisitor(AbstractUser user) throws InterruptedException {
        return false;
    }

    @Override
    public boolean joinQueue(AbstractUser user) throws InterruptedException {
        return false;
    }

    @Override
    public boolean sendMessage(AbstractUser user, Message msg) throws InterruptedException {
        return false;
    }

    @Override
    public MsgSendService getMsgSendService() {
        return null;
    }
}
