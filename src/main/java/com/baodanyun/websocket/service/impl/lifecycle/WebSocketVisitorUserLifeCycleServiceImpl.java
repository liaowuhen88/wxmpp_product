package com.baodanyun.websocket.service.impl.lifecycle;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.msg.status.StatusMsg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.service.MsgSendService;
import com.baodanyun.websocket.service.WebSocketService;
import com.baodanyun.websocket.util.JSONUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by liaowuhen on 2017/3/6.
 */
@Service("wvUserLifeCycleService")
public class WebSocketVisitorUserLifeCycleServiceImpl extends VisitorUserLifeCycleServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketVisitorUserLifeCycleServiceImpl.class);

    @Autowired
    protected WebSocketService webSocketService;

    @Autowired
    @Qualifier("webSocketMsgSendService")
    protected MsgSendService msgSendService;

    @Override
    public void logout(AbstractUser user) throws InterruptedException {
        super.logout(user);

        Visitor visitor = (Visitor) user;
        AbstractUser customer= visitor.getCustomer();

        getMsgSendService().sendSMMsgToVisitor(visitor,customer,StatusMsg.Status.customerOffline);
        getMsgSendService().sendSMMsgToCustomer(visitor,customer,StatusMsg.Status.offline);

        //关闭ws连接
        try {
            webSocketService.closed(user.getId());
        } catch (IOException e) {
            logger.info("closed session error", e);
        }
    }

    @Override
    public Msg getMsg(AbstractUser user, String content) {
        Visitor visitor = (Visitor) user;
        if (!StringUtils.isEmpty(content)) {
            Msg msg = Msg.handelMsg(content);
            // 因为有转接，所有目的地址需要重新设置
            String destId = visitor.getCustomer().getId();
            msg.setTo(destId);
            msg.setFrom(visitor.getId());
            if (msg != null && !StringUtils.isEmpty(msg.getFrom()) && !StringUtils.isEmpty(destId)) {
               /* XmppContentMsg xm = new XmppContentMsg();
                xm.setContentType(msg.getContentType());
                xm.setContent(msg.getContent());
                xm.setFromType("41");
                xm.setFrom(msg.getFrom());
                xm.setRealFrom(msg.getFrom());
                xm.setGroupName(msg.getFromName());*/
                msg.setContent(msg.getContent());

                return msg;
            } else {
                logger.error("handleSendMsg from or to is blank" + JSONUtil.toJson(msg));
            }
        } else {
            logger.error("content is empty");
        }
        return null;
    }

    @Override
    public MsgSendService getMsgSendService() {
        return msgSendService;
    }
}
