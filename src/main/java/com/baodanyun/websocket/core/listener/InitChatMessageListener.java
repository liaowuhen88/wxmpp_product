package com.baodanyun.websocket.core.listener;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.service.ConversationService;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.MsgService;
import com.baodanyun.websocket.service.OfmucroomService;
import com.baodanyun.websocket.service.impl.msgHandle.MsgHandelList;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.SpringContextUtil;
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

    ConversationService conversationService = SpringContextUtil.getBean("conversationService", ConversationService.class);
    MsgService msgService = SpringContextUtil.getBean("msgServiceImpl", MsgService.class);
    OfmucroomService ofmucroomService = SpringContextUtil.getBean("ofmucroomServiceImpl", OfmucroomService.class);
    MsgHandelList msgHandelList = SpringContextUtil.getBean("msgHandelList", MsgHandelList.class);

    private MsgSendControl msgSendControl;
    private AbstractUser user;

    public InitChatMessageListener(AbstractUser user, MsgSendControl msgSendControl) {
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
            logger.info("{} xmpp receive message :" + JSONUtil.toJson(msg), this.getUser().getId());

            Msg sendMsg = msgSendControl.getMsg(msg);

            if (null == sendMsg) {
                return;
            }
            sendMsg.setAgency(user.isAgency());

            msgHandelList.handel(user, msg, sendMsg);

            logger.info(JSONUtil.toJson(sendMsg));

            if (null != sendMsg) {

                sendMsg.setOpenId(user.getOpenId());
                msgSendControl.sendMsg(sendMsg);
            }

        } catch (Exception e) {
            logger.error("msgSendControl.sendMsg error", e);
        }
    }
}
