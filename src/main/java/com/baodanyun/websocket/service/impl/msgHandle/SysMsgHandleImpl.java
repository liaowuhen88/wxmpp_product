package com.baodanyun.websocket.service.impl.msgHandle;

import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.Ofvcard;
import com.baodanyun.websocket.service.MsgService;
import com.baodanyun.websocket.service.VcardService;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2017/9/12.
 */
@Service("sysMsgHandle")
public class SysMsgHandleImpl extends AbstractMsgHandleService {
    protected static Logger logger = LoggerFactory.getLogger(SysMsgHandleImpl.class);

    @Autowired
    private VcardService vcardService;

    @Autowired
    private MsgService msgService;

    private static String getTypeName(String type) {
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

    @Override
    public boolean canHandel(Message msg, Msg sendMsg) {
        String from = msg.getFrom();
        String[] ffs = from.split("__");
        if (null != ffs && ffs.length > 2 && ffs[0].startsWith("system")) {
            return true;
        }
        return false;
    }

    @Override
    public void handel(AbstractUser user, Message msg, Msg sendMsg) {
        //system@126xmpp/__xvjq48_8497@126xmpp__xvjq48@126xmpp_1
        //system@126xmpp/__xvjq48_8497@conference.126xmpp/昵称__xvjq48@126xmpp_1

        String from = msg.getFrom();
        String[] ffs = from.split("__");
        String realFrom = ffs[1];
        String machine = ffs[2];
        String type = ffs[3];

        sendMsg.setFrom(XMPPUtil.removeRoomSource(realFrom));
        sendMsg.setFromType(Msg.fromType.system);

        updateMsg(type, machine, sendMsg);

    }

    /**
     * 更新消息来源名称
     */
    private void updateMsg(String type, String machine, Msg sendMsg) {
        Ofvcard vCard = null;
        try {
            vCard = vcardService.loadVcard(XMPPUtil.jidToName(machine));
        } catch (BusinessException e) {
            logger.error("error", e);
        }

        ConversationMsg conversation = new ConversationMsg();
        msgService.initByVCard(conversation, vCard);

        if (null != conversation) {
            sendMsg.setFromName(conversation.getFromName() + "【" + getTypeName(type) + "】");
        } else {
            if (StringUtils.isEmpty(sendMsg.getFromName())) {
                sendMsg.setFromName("系统消息【" + getTypeName(type) + "】");
            }
        }
    }

}
