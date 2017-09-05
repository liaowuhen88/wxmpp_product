package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.util.Config;
import com.baodanyun.websocket.util.EmojiUtil;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * Created by liaowuhen on 2017/1/12.
 * <p/>
 * 把消息发送到客服pc端的消息队列控制器
 */
@Service
public class MsgSendControl {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public WebSocketService webSocketService;

    @Autowired
    public MessageSendToWeixin messageSendToWeixin;

    @Autowired
    public UserServer userServer;

    @Autowired
    public UserCacheServer userCacheServer;

    public boolean sendMsg(Msg msg) throws InterruptedException {
        webSocketService.produce(msg);
        return true;
    }

    public Msg getMsg(Message msg) {
        Msg sendMsg = null;
        String body = msg.getBody();
        if (StringUtils.isNotBlank(body)) {
            body = EmojiUtil.tranformEemojiContent(body);//文本内容替换表情
            sendMsg = new Msg(body);
            String from = XMPPUtil.removeRoomSource(msg.getFrom());
            sendMsg.setFrom(from);
            if(StringUtils.isEmpty(msg.getSubject())){
                sendMsg.setContentType("text");
            }else {
                sendMsg.setContentType(msg.getSubject());
            }

            if(StringUtils.isEmpty(msg.getTo())){
                sendMsg.setTo(Config.controlId+"@126xmpp");
            }else {
                String to = XMPPUtil.removeSource(msg.getTo());
                sendMsg.setTo(to);
            }
            sendMsg.setId(UUID.randomUUID().toString());
            //sendMsg.setId(msg.getStanzaId());
        }else {
            logger.info("body is null");
        }
        return sendMsg;
    }

    /**
     * 欢迎语
     * 客服
     *
     * @param user
     * @return
     */

    public Msg getMsgHelloToCustomer(AbstractUser user) {
        logger.info("user--->" + JSONUtil.toJson(user));
        String body = "您好，请问有什么可以帮您的？";
        Msg sendMsg = new Msg(body);
        String to = user.getId();
        String from = user.getId();
        String type = Msg.Type.msg.toString();
        Long ct = new Date().getTime();

        sendMsg.setType(type);
        sendMsg.setContentType(Msg.MsgContentType.text.toString());
        sendMsg.setTo(to);
        sendMsg.setFrom(from);
        sendMsg.setCt(ct);
        // 获取发送端用户
        sendMsg.setIcon(user.getIcon());
        sendMsg.setFromName(user.getNickName());

        return sendMsg;
    }

    public Msg getMsgHelloToVisitor(Visitor user) {
        logger.info("user--->" + JSONUtil.toJson(user));
        String body = "您好，请问有什么可以帮您的？";
        Msg sendMsg = new Msg(body);
        String from = user.getCustomer().getId();
        String to = user.getId();
        if (user.getType() == 1) {
            to = XMPPUtil.removeSource(user.getOpenId());
            sendMsg.setToType(user.getType());
        } else {
            sendMsg.setToType(0);
        }

        String type = Msg.Type.msg.toString();
        Long ct = new Date().getTime();

        sendMsg.setType(type);
        sendMsg.setContentType(Msg.MsgContentType.text.toString());
        sendMsg.setFrom(from);
        sendMsg.setTo(to);
        sendMsg.setCt(ct);
        sendMsg.setIcon(user.getIcon());
        // 获取发送端用户
       /* AbstractUser u = userServer.getUser(from);
        if (StringUtils.isEmpty(u.getIcon())) {
            logger.info("user " + from + "----------user icon is null");
        } else {
            sendMsg.setIcon(u.getIcon());
        }
        if (StringUtils.isEmpty(u.getNickName())) {
            logger.info("user " + from + "----------user NickName is null");
        } else {
            sendMsg.setFromName(u.getNickName());
        }*/
        return sendMsg;
    }

}
