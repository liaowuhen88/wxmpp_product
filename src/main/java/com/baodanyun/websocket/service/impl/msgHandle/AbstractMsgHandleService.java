package com.baodanyun.websocket.service.impl.msgHandle;

import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.service.ConversationService;
import com.baodanyun.websocket.service.MsgHandleService;
import com.baodanyun.websocket.service.MsgService;
import com.baodanyun.websocket.service.OfmucroomService;
import com.baodanyun.websocket.util.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by liaowuhen on 2017/9/12.
 */
public class AbstractMsgHandleService implements MsgHandleService {
    protected static Logger logger = LoggerFactory.getLogger(AbstractMsgHandleService.class);
    @Autowired
    public ConversationService conversationService;

    @Autowired
    public OfmucroomService ofmucroomService;

    @Autowired
    public MsgService msgService;


    @Override
    public boolean canHandel(Message msg, Msg sendMsg) {
        return false;
    }

    @Override
    public void handel(AbstractUser user, Message msg, Msg sendMsg) {

    }

    @Override
    public ConversationMsg initConversationMsg(AbstractUser user, Msg sendMsg) {
        ConversationMsg conversation = initByCache(user, sendMsg);

        return conversation;
    }

    @Override
    public ConversationMsg initByCache(AbstractUser user, Msg sendMsg) {
        try {
            String json = conversationService.get(user.getId(), sendMsg.getFrom());
            if (StringUtils.isNotEmpty(json)) {
                return JSONUtil.toObject(ConversationMsg.class, json);
            }
        } catch (Exception e) {
            logger.error("error", e);
        }

        return null;
    }
}
