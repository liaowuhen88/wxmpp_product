package com.baodanyun.websocket.service.impl.lifecycle;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.msg.status.StatusMsg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.service.MsgSendService;
import com.baodanyun.websocket.util.CommonConfig;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by liaowuhen on 2017/3/6.
 */
@Service
public abstract class VisitorUserLifeCycleServiceImpl extends UserLifeCycleServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(VisitorUserLifeCycleServiceImpl.class);

    @Override
    public boolean login(AbstractUser user) throws IOException, XMPPException, SmackException, BusinessException, InterruptedException {
        boolean flag = super.login(user);
        Visitor visitor = (Visitor) user;
        visitorListener.login(visitor);

        // 加好友动作
        if(flag){
            String customerJid = visitor.getCustomer().getId();
            xmppService.roster(visitor.getId(), customerJid);
            vcardService.InitVisitor(visitor);
            return true;
        }
        return false;
    }

    @Override
    public void logout(AbstractUser user) throws InterruptedException {
        super.logout(user);
        Visitor visitor = (Visitor) user;
        visitorListener.logOut(visitor);
        String customerJid = visitor.getCustomer().getId();

    }

    @Override
    public boolean init(AbstractUser user) throws InterruptedException, SmackException.NotLoggedInException, SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, BusinessException {
        Visitor visitor = (Visitor) user;
        super.init(user);
        // 通知用户
        AbstractUser customer = visitor.getCustomer();

        getMsgSendService().sendSMMsgToVisitor(visitor, customer, StatusMsg.Status.loginSuccess);

        getMsgSendService().sendSMMsgToVisitor(user, customer, StatusMsg.Status.initSuccess);
        return true;
    }

    @Override
    public boolean online(AbstractUser user) throws InterruptedException, BusinessException, SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {
        super.online(user);
        Visitor visitor = (Visitor) user;
        boolean flag = joinQueue(visitor);
        return flag;
    }

    @Override
    public MsgSendService getMsgSendService() {
        return super.getMsgSendService();
    }

    @Override
    public boolean joinQueue(AbstractUser user) throws InterruptedException {
        Visitor visitor = (Visitor) user;
        join(visitor);
        AbstractUser customer = visitor.getCustomer();

        getMsgSendService().sendSMMsgToVisitor(visitor, customer, StatusMsg.Status.onlineQueueSuccess);
        //getMsgSendService().sendSMMsgToCustomer(visitor, customer, StatusMsg.Status.onlineQueueSuccess);

        getMsgSendService().sendHelloToVisitor(visitor);
        getMsgSendService().sendHelloToCustomer(customer);
        return true;

    }

    private boolean join(Visitor visitor) throws InterruptedException {
        //如果上线 重置等待对象
        String cjid = visitor.getCustomer().getId();

        logger.info("保存到缓存--->" + userCacheServer.add(CommonConfig.USER_VISITOR, visitor));

        logger.info("保存到缓存--->" + userCacheServer.add(CommonConfig.USER_ONLINE, cjid, visitor));

        return true;
    }

    /**
     * 参数为访客
     *
     * @param user
     * @return
     * @throws InterruptedException
     */
    @Override
    public boolean uninstallVisitor(AbstractUser user) throws InterruptedException {
        Visitor visitor = (Visitor) user;
        AbstractUser customer = visitor.getCustomer();

        getMsgSendService().sendSMMsgToCustomer(visitor, customer, StatusMsg.Status.offline);

        return true;
    }

    @Override
    public Msg receiveMessage(AbstractUser user, String content) throws InterruptedException, SmackException.NotConnectedException, BusinessException {
        Msg msg = getMsg(user,content);
        if(null != msg){
            if (user.isAgency()) {
                xmppService.sendMessageAgent(msg, user.getRealFrom());
            } else {
                xmppService.sendMessageNoChange(msg);
            }

        }
        Visitor visitor = (Visitor) user;
        visitorListener.chat(msg.getContent(), visitor);
        return msg;
    }

}
