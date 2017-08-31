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
import com.baodanyun.websocket.util.SpringContextUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
    private MsgSendControl msgSendControl;
    private AbstractUser user;

    public InitChatMessageListener(AbstractUser user, MsgSendControl msgSendControl) {
        this.user = user;
        this.msgSendControl = msgSendControl;
    }

    public static void main(String[] args) {
        /* String realFrom = "__xvjq39@126xmpp";
         String[] froms = realFrom.split("__");
         System.out.println(froms[0]);
         System.out.println(froms[1]);*/

        System.out.print(getTypeName("a"));

    }

    public static String getTypeName(String type) {
        // 1：固定回复，2：智能行情，3：关键词回复，4：机器人回复，5：定时回复  6：加好友自动回复  7：好友进群自动回复

        if (NumberUtils.isNumber(type)) {
            Integer index = Integer.valueOf(type);
            switch (index) {
                case 1:
                    return "固定回复";
                case 2:
                    return "智能行情";
                case 3:
                    return "关键词回复";
                case 4:
                    return "机器人回复";
                case 5:
                    return "定时回复";
                case 6:
                    return "加好友自动回复";
                case 7:
                    return "好友进群自动回复";
            }
        }

        logger.info("index {}", type);
        return "未知类型";

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
            String from = msg.getFrom();
            String to = msg.getTo();

            // 是否是系统消息
            boolean isSysMsg = dealSystem(sendMsg, from);
            if (!isSysMsg) {
                dealH5(sendMsg, from, to);
            }

            logger.info(JSONUtil.toJson(sendMsg));
            if (null != sendMsg) {
                // 手机app端发送过来的数据subject 为空
                ConversationMsg conversation = null;
                String json = conversationService.get(user.getId(), sendMsg.getFrom());
                if (StringUtils.isNotEmpty(json)) {
                    conversation = JSONUtil.toObject(ConversationMsg.class, json);
                }
                if (null != conversation) {
                    logger.info(" user {}, realFrom {} isExist", user.getId(), sendMsg.getFrom());
                } else {
                    logger.info(" user {}, realFrom {} notExist", user.getId(), sendMsg.getFrom());
                    // 为了解决系统发送的群消息
                    if (XMPPUtil.isRoom(sendMsg.getFrom())) {
                        Ofmucroom ofmucroom = ofmucroomService.selectByPrimaryKey((long) 1, XMPPUtil.getRoomName(sendMsg.getFrom()));
                        conversation = msgService.getNewRoomJoines(sendMsg.getFrom(), ofmucroom, user.getId());
                    } else {
                        conversation = msgService.getNewPersionalJoines(sendMsg.getFrom(), user);
                    }
                    //msgSendControl.sendMsg(conversation);
                }

                if (!isSysMsg) {
                    sendMsg.setFromName(conversation.getFromName());
                    sendMsg.setIcon(conversation.getIcon());
                }

                sendMsg.setOpenId(user.getOpenId());
                msgSendControl.sendMsg(sendMsg);
            }

        } catch (Exception e) {
            logger.error("msgSendControl.sendMsg error", e);
        }
    }

    /**
     * 设置真正to地址
     *
     * @param sendMsg
     * @param from
     * @param to
     */

    public boolean dealH5(Msg sendMsg, String from, String to) {
        if (user.isAgency()) {
            String realTo = "";
            String[] froms = from.split("_");
            if (froms.length == 1) {
                realTo = XMPPUtil.jidToName(to) + "_" + froms[0];
            } else if (froms.length == 2) {
                realTo = XMPPUtil.jidToName(to) + "_" + froms[1];
            }
            sendMsg.setTo(realTo);
            return true;
        }
        return false;
    }

    /**
     * 初始化from地址
     *
     * @param sendMsg
     * @param from
     */
    public boolean dealSystem(Msg sendMsg, String from) {
        //system@126xmpp/__xvjq48_8497@126xmpp__xvjq48@126xmpp_1
        //system@126xmpp/__xvjq48_8497@conference.126xmpp/昵称__xvjq48@126xmpp_1
        String[] ffs = from.split("__");
        if (null != ffs && ffs.length > 1 && ffs[0].startsWith("system")) {

            String realFrom = ffs[1];
            String machine = ffs[2];
            String type = ffs[3];
            sendMsg.setFrom(XMPPUtil.removeRoomSource(realFrom));
            sendMsg.setFromType(Msg.fromType.system);

            ConversationMsg conversation = null;
            String json = null;
            try {
                json = conversationService.get(user.getId(), sendMsg.getFrom());
                if (StringUtils.isNotEmpty(json)) {
                    conversation = JSONUtil.toObject(ConversationMsg.class, json);
                } else {
                    conversation = msgService.getNewPersionalJoines(machine, user);
                }

            } catch (Exception e) {
                logger.error("error", e);
            }

            if (null != conversation) {
                if (StringUtils.isEmpty(sendMsg.getFromName())) {
                    sendMsg.setFromName(conversation.getFromName() + "【" + getTypeName(type) + "】");
                }
            } else {
                if (StringUtils.isEmpty(sendMsg.getFromName())) {
                    sendMsg.setFromName("系统消息【" + getTypeName(type) + "】");
                }
            }


            return true;
        }

        return false;

    }
}
