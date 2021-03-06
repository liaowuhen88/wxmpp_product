package com.baodanyun.websocket.core.listener;

import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.model.Ofmucroom;
import com.baodanyun.websocket.service.ConversationService;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.MsgService;
import com.baodanyun.websocket.service.OfmucroomService;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liaowuhen on 2017/5/11.
 */
public class UcMessageListener implements MessageListener {
    protected static Logger logger = LoggerFactory.getLogger(UcMessageListener.class);

    private MsgSendControl msgSendControl;
    private AbstractUser user;
    private ConversationService conversationService;
    private OfmucroomService ofmucroomService;
    private MsgService msgService;

    public UcMessageListener(MsgSendControl msgSendControl, AbstractUser user, ConversationService conversationService, OfmucroomService ofmucroomService, MsgService msgService) {
        this.msgSendControl = msgSendControl;
        this.user = user;
        this.conversationService = conversationService;
        this.ofmucroomService = ofmucroomService;
        this.msgService = msgService;
    }

    public static void main(String[] args) {
        String from = "xvql1048@conference.126xmpp/xvjq64_g197001__A\\u003cspan class\\u003d\\\"emoji emoji1f4a7\\\"\\u003e\\u003c/span\\u003e妤婷";

        if (!StringUtils.isEmpty(from) && from.contains("/")) {
            String realFrom = from.split("/")[1];
            String[] keys = realFrom.split("__");
            if (null != keys && keys.length == 2) {
                String key = keys[0];
                logger.info("key {} is null", key);
            }
        }

    }

    public AbstractUser getUser() {
        return user;
    }

    public void setUser(AbstractUser user) {
        this.user = user;
    }

    @Override
    public void processMessage(Message msg) {
        logger.info("{}接收到群组信息:" + JSONUtil.toJson(msg), this.getUser().getId());
        try {
            Msg sendMsg = msgSendControl.getMsg(msg);
            if (null == sendMsg) {
                logger.warn("sendMsg is null");
                return;
            }
            String realRoom = XMPPUtil.removeRoomSource(sendMsg.getFrom());
            ConversationMsg conversation = null;
            String json = conversationService.get(user.getId(), realRoom);
            if (StringUtils.isNotEmpty(json)) {
                conversation = JSONUtil.toObject(ConversationMsg.class, json);
            }

            if (null != conversation) {
                logger.info(" user {}, room {} isExist", user.getId(), realRoom);
            } else {
                logger.info(" user {}, room {} notExist", user.getId(), realRoom);
                Ofmucroom ofmucroom = ofmucroomService.selectByPrimaryKey((long) 1, XMPPUtil.getRoomName(realRoom));
                conversation = msgService.getNewRoomJoines(realRoom, ofmucroom, user.getId(), user.getAppkey(), user);
                //logger.info(JSONUtil.toJson(conversation));
                // msgSendControl.sendMsg(msgConversation);
            }

            if (!StringUtils.isEmpty(msg.getFrom()) && msg.getFrom().contains("/")) {
                String realFrom = msg.getFrom().split("/")[1];
                if (user.getLoginUsername().equals(realFrom)) {
                    // 客服自己发送的群消息，不发送到前端
                    return;
                }
                String[] keys = realFrom.split("__");
                if (null != keys && keys.length == 2) {
                    sendMsg.setFromName(keys[1]);
                    String key = keys[0];
                    if (null != conversation.getGroupUserMap().get(key)) {
                        sendMsg.setIcon(conversation.getGroupUserMap().get(key).getIcon());
                    } else {
                        logger.info("key {} is null", key);
                    }
                }
            } else {
                logger.info("no resource");
            }


            if (null != sendMsg) {
                // 手机app端发送过来的数据subject 为空
                sendMsg.setOpenId(user.getOpenId());
                msgSendControl.sendMsg(sendMsg);
            }
        } catch (Exception e) {
            logger.error("msgSendControl.sendMsg error", e);
        }

    }

    @Override
    public int hashCode() {
        return this.getUser().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        UcMessageListener e = (UcMessageListener) obj;
        return e.getUser().equals(this.getUser());
    }


}
