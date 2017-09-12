package com.baodanyun.websocket.service.impl.msgHandle;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.util.XMPPUtil;
import org.jivesoftware.smack.packet.Message;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2017/9/12.
 * <p/>
 * 客服发送消息到用户
 */
@Service("h5ToUMsgHandle")
public class H5ToUserMsgHandleImpl extends AbstractMsgHandleService {
    @Override
    public boolean canHandel(Message msg, Msg sendMsg) {
        String to = msg.getTo();
        //"to":"xvsh7-web@126xmpp","from":"xvsh7@126xmpp/Smack_3903276471@126xmpp"
        if (to.contains("-web@126xmpp")) {
            return true;
        }
        return false;
    }

    /**
     * 修改to地址发送到指定的web接入用户
     *
     * @param user
     * @param msg     // 客服到用户
     * @param sendMsg "to":"xvsh7-web@126xmpp","from":"xvsh7@126xmpp/Smack_3903276471@126xmpp"
     */
    @Override
    public void handel(AbstractUser user, Message msg, Msg sendMsg) {
        String from = msg.getFrom();
        String to = msg.getTo();

        String realTo = "";
        String[] froms = from.split("_");
        if (froms.length == 1) {
            realTo = XMPPUtil.jidToName(to) + "_" + froms[0];
        } else if (froms.length == 2) {
            realTo = XMPPUtil.jidToName(to) + "_" + froms[1];
        }
        sendMsg.setTo(realTo);

        sendMsg.setFromName(user.getNickName());
        sendMsg.setIcon(user.getIcon());
    }
}
