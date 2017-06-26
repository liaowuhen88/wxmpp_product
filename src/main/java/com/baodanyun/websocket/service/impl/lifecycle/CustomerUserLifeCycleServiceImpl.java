package com.baodanyun.websocket.service.impl.lifecycle;

import com.baodanyun.websocket.bean.msg.status.StatusMsg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.Customer;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.service.ConversationService;
import com.baodanyun.websocket.util.CommonConfig;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by liaowuhen on 2017/3/6.
 */
@Service
public abstract  class CustomerUserLifeCycleServiceImpl extends UserLifeCycleServiceImpl {

    @Autowired
    public ConversationService conversationService;

    @Override
    public boolean online(AbstractUser user) throws InterruptedException, BusinessException, SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {
        super.online(user);
        conversationService.clear(user.getId());
        logger.info("保存到缓存[USER_CUSTOMER]["+user.getId()+"]--->" + userCacheServer.add(CommonConfig.USER_CUSTOMER, user));
        return true;
    }

    @Override
    public boolean login(AbstractUser user) throws IOException, XMPPException, SmackException, BusinessException, InterruptedException {
        return super.login(user);
    }

    @Override
    public boolean init(AbstractUser user) throws InterruptedException, SmackException.NotLoggedInException, BusinessException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {
        Customer customer = (Customer) user;

        super.init(customer);
        // 通知用户
        getMsgSendService().sendSMMsgToCustomer(customer, StatusMsg.Status.loginSuccess);

        getMsgSendService().sendSMMsgToCustomer(customer, StatusMsg.Status.initSuccess);
        return true;
    }

}
