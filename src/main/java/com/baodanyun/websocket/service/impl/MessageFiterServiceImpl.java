package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.event.ComputationalCostsEvent;
import com.baodanyun.websocket.event.MessageArchiveAdapterEvent;
import com.baodanyun.websocket.model.MessageArchiveAdapter;
import com.baodanyun.websocket.service.JedisService;
import com.baodanyun.websocket.service.MessageFiterService;
import com.baodanyun.websocket.util.EventBusUtils;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liaowuhen on 2017/7/31.
 */

@Service
public class MessageFiterServiceImpl implements MessageFiterService {
    public static final String SWITCH = "xvkefu_charging";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JedisService jedisService;

    @Override
    public void filter(String jid, Msg msg) {
        boolean flag = isEncrypt(jid, msg.getFrom());
        if (flag) {
            /**
             * 消息加密
             */
            MessageArchiveAdapter ma = new MessageArchiveAdapter();
            ma.setMessageid(msg.getId());
            ma.setContentType(msg.getContentType());
            ma.setContent(msg.getContent());
            ma.setFromJid(msg.getFrom());
            ma.setToJid(msg.getTo());

            MessageArchiveAdapterEvent me = new MessageArchiveAdapterEvent();
            me.setMessageArchiveAdapter(ma);
            EventBusUtils.post(me);

            msg.setEncrypt(true);
            if (Msg.MsgContentType.image.toString().equals(msg.getContentType())) {
                msg.setContent("您收到一张图片");
                msg.setContentType(Msg.MsgContentType.text.toString());
            } else if (Msg.MsgContentType.video.toString().equals(msg.getContentType())) {
                msg.setContent("您收到一段视频");
                msg.setContentType(Msg.MsgContentType.text.toString());
            } else if (Msg.MsgContentType.audio.toString().equals(msg.getContentType())) {
                msg.setContent("您收到一段音频");
                msg.setContentType(Msg.MsgContentType.text.toString());
            } else if (Msg.MsgContentType.text.toString().equals(msg.getContentType())) {
                msg.setContent("您收到一条消息");
            }

        } else {
            //TODO 计费接口
            computationalCosts(jid, msg);
        }
    }


    @Override
    public List<ConversationMsg> initCollections(String jid, List<ConversationMsg> collections) {
        // 是否加密
        if (null != collections && collections.size() > 0) {
            List<ConversationMsg> list = new ArrayList<>();
            String statue1 = jedisService.getValue(SWITCH);
            String status2 = jedisService.getValue(jid);

            for (ConversationMsg cm : collections) {
                boolean isEncrypt = isEncrypt(statue1, status2, jid, cm.getFrom());
                if (isEncrypt) {
                    cm.setOnlineStatus(ConversationMsg.OnlineStatus.encrypt);
                } else {
                    cm.setOnlineStatus(ConversationMsg.OnlineStatus.online);
                }
            }

            return list;
        }

        return null;
    }


    @Override
    public boolean isEncrypt(String jid, String from) {
        String statue1 = jedisService.getValue(SWITCH);
        String status2 = jedisService.getValue(jid);

        return isEncrypt(statue1, status2, jid, from);
    }

    @Override
    public boolean isEncrypt(String statue1, String status2, String jid, String from) {
        // 是否加密
        boolean flag = false;
        logger.info("总开关---key:{}----statue1:{}", SWITCH, statue1);
        // 总开关为计费模式
        if ("0".equals(statue1)) {
            // 坐席是否欠费
            logger.info("坐席Jid {} ------ statue2:{}", jid, status2);
            if ("0".equals(status2)) {
                flag = true;
            } else {
                String key3 = XMPPUtil.removeRoomSource(from);
                String status3 = jedisService.getValue(key3);
                logger.info("好友或者群 {} ------ status3:{}", key3, status3);
                if ("0".equals(status3)) {
                    flag = true;
                }
            }
        }

        return flag;
    }

    @Override
    public boolean computationalCosts(String jid, Msg msg) {
        Msg clone = SerializationUtils.clone(msg);
        ComputationalCostsEvent cce = new ComputationalCostsEvent(jid, clone);
        EventBusUtils.post(cce);

        return true;
    }

    @Override
    public boolean computationalCosts(String jid, MessageArchiveAdapter ma) {
        Msg msg = new Msg();
        msg.setContent(ma.getContent());
        msg.setContentType(ma.getContentType());
        msg.setFrom(ma.getFromJid());
        msg.setTo(ma.getToJid());
        msg.setCt(ma.getCt().getTime());
        msg.setType(Msg.Type.msg.toString());
        ComputationalCostsEvent cce = new ComputationalCostsEvent(jid, msg);
        EventBusUtils.post(cce);
        return true;
    }

   /* @Override
    public boolean isEncrypt(String jid, String from) {
       *//* boolean flag = false;
        String key3 = XMPPUtil.removeRoomSource(from);
        String status3 = jedisService.getValue(key3);
        logger.info("好友或者群 {} ------ status3:{}", key3, status3);
        if ("1".equals(status3)) {
            flag = true;
        }

        return flag;*//*

        return !dispaly(jid, from);
    }*/

}
