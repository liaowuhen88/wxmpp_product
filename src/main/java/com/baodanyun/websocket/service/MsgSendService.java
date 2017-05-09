package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.msg.status.StatusMsg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2017/3/6.
 */
@Service
public interface MsgSendService {

    /**
     * 获取发送到客户端的状态消息
     * @param visitor
     * @param customer
     * @return
     */
    StatusMsg getSMMsgSendTOVisitor(AbstractUser visitor,AbstractUser customer);

    StatusMsg getSMMsgSendTOVisitor(AbstractUser customer);
    /**
     * 获取发送到客服端的状态消息
     * @param visitor
     * @param customer
     * @return
     */

    StatusMsg getSMMsgSendTOCustomer(AbstractUser visitor,AbstractUser customer);

    /**
     * 发送状态消息到客户端
     * @param visitor
     * @param customer
     * @return
     */

    void sendSMMsgToVisitor(AbstractUser visitor,AbstractUser customer,StatusMsg.Status status) throws InterruptedException;

    void sendSMMsgToVisitor(AbstractUser customer,StatusMsg.Status status) throws InterruptedException;


    /**
     * 发送状态消息到客服
     * @param visitor
     * @param customer
     * @return
     */
    void sendSMMsgToCustomer(AbstractUser visitor,AbstractUser customer,StatusMsg.Status status) throws InterruptedException;

    void sendSMMsgToCustomer(AbstractUser customer,StatusMsg.Status status) throws InterruptedException;

     void sendHelloToVisitor(AbstractUser visitor) throws InterruptedException;

    void sendHelloToCustomer(AbstractUser customer) throws InterruptedException;

    void produce( Msg msgBean) throws InterruptedException;

}
