package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.msg.status.StatusMsg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.service.MessageSendToWeixin;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.MsgSendService;
import com.baodanyun.websocket.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2017/3/6.
 */
@Service("winXinMsgSendService")
public class WeiXInMsgSendServiceImpl extends MsgSendServiceImpl {
    @Autowired
    private MsgSendControl msgSendControl;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    public MessageSendToWeixin messageSendToWeixin;

    @Override
    public StatusMsg getSMMsgSendTOVisitor(AbstractUser visitor, AbstractUser customer) {
        return null;
    }

    @Override
    public void sendSMMsgToVisitor(AbstractUser visitor, AbstractUser customer, StatusMsg.Status status) throws InterruptedException {

    }

    @Override
    public void sendSMMsgToCustomer(AbstractUser customer, StatusMsg.Status status) throws InterruptedException {

    }

    @Override
    public void sendHelloToVisitor(AbstractUser visitor) {
        Visitor visi = (Visitor) visitor;
        Msg msg = msgSendControl.getMsgHelloToVisitor(visi);
        messageSendToWeixin.send(msg, msg.getTo(), msg.getId());
    }

    @Override
    public void sendHelloToCustomer(AbstractUser customer) throws InterruptedException {
        Msg msg = msgSendControl.getMsgHelloToCustomer(customer);
        webSocketService.produce(msg);
    }

    @Override
    public void produce(Msg msgBean) throws InterruptedException {

    }
}
