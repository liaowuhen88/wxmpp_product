package com.baodanyun.websocket.core.listener;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.event.ConversationRoomEvent;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.util.EventBusUtils;
import com.baodanyun.websocket.util.JSONUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by liaowuhen on 2017/5/11.
 */
public class UcMessageListener implements MessageListener {
    private static Logger logger = Logger.getLogger(UcMessageListener.class);

    private MsgSendControl msgSendControl;
    private AbstractUser user;


    public UcMessageListener(MsgSendControl msgSendControl,AbstractUser user){
        this.msgSendControl = msgSendControl;
        this.user = user;
    }


    @Override
    public void processMessage(Message msg) {
         logger.info("接收到群组信息:"+ JSONUtil.toJson(msg));
        try {
            Msg sendMsg = msgSendControl.getMsg(msg);

            ConversationRoomEvent je = new ConversationRoomEvent(user, sendMsg.getFrom());

            EventBusUtils.post(je);
            if(!StringUtils.isEmpty(msg.getFrom()) && msg.getFrom().contains("/")){
                String realFrom = msg.getFrom().split("/")[1];
                if(user.getLoginUsername().equals(realFrom)){
                    // 客服自己发送的群消息，不发送到前端
                    return ;
                }
                sendMsg.setFromName(realFrom);
            }else {
                sendMsg.setFromName(msg.getFrom());
            }

            if (null != sendMsg) {
                // 手机app端发送过来的数据subject 为空
                sendMsg.setOpenId(user.getOpenId());
                msgSendControl.sendMsg(sendMsg);
            }
        } catch (Exception e) {
            logger.error("msgSendControl.sendMsg error");
        }

    }
}
