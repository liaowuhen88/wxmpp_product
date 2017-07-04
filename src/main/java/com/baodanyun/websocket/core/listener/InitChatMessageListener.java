package com.baodanyun.websocket.core.listener;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.service.ConversationService;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.MsgService;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.SpringContextUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.SerializationUtils;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liaowuhen on 2017/5/11.
 */
public class InitChatMessageListener implements ChatMessageListener {
    protected static Logger logger = LoggerFactory.getLogger(InitChatMessageListener.class);
    ConversationService conversationService = SpringContextUtil.getBean("conversationServiceImpl", ConversationService.class);
    MsgService msgService = SpringContextUtil.getBean("msgServiceImpl", MsgService.class);
    private MsgSendControl msgSendControl;
    private AbstractUser user;

    public InitChatMessageListener(AbstractUser user, MsgSendControl msgSendControl){
        this.user = user;
        this.msgSendControl = msgSendControl;
    }

    public AbstractUser getUser() {
        return user;
    }

    public void setUser(AbstractUser user) {
        this.user = user;
    }

    @Override
    public void processMessage(Chat chat, Message msg) {
        try {
            logger.info("{} xmpp receive message :" + JSONUtil.toJson(msg),this.getUser().getId());
            Msg sendMsg = msgSendControl.getMsg(msg);
            String from = msg.getFrom();
            String realFrom = XMPPUtil.removeRoomSource(from);
            if (null != sendMsg) {
                // 手机app端发送过来的数据subject 为空
                logger.info(msg.getFrom());
                boolean isExist = conversationService.isExist(user.getId(), realFrom);

                if (isExist) {
                    logger.info(" user {}, realFrom {} isExist", user.getId(), realFrom);
                } else {
                    logger.info(" user {}, realFrom {} notExist", user.getId(), realFrom);
                    Msg cloneMsg = (Msg) SerializationUtils.clone(sendMsg);
                    Msg conversation = msgService.getNewPersionalJoines(realFrom, user, cloneMsg);
                    msgSendControl.sendMsg(conversation);
                    conversationService.addConversations(user.getId(), realFrom);
                }

                sendMsg.setOpenId(user.getOpenId());
                msgSendControl.sendMsg(sendMsg);
            }

        } catch (Exception e) {
            logger.error("msgSendControl.sendMsg error", e);
        }
    }
}
