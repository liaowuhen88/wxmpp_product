package com.baodanyun.websocket.service.impl;

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

/**
 * Created by liaowuhen on 2017/7/31.
 */

@Service
public class MessageFiterServiceImpl implements MessageFiterService {
    public static final String MM = "_charrging";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JedisService jedisService;

    @Override
    public void filter(String jid, Msg msg) {
        boolean flag = dispaly(jid, msg.getFrom());
        if (!flag) {
            /**
             * 消息加密
             */
            MessageArchiveAdapter ma = new MessageArchiveAdapter();
            ma.setMessageid(msg.getId());
            ma.setContent(msg.getContent());

            MessageArchiveAdapterEvent me = new MessageArchiveAdapterEvent();
            me.setMessageArchiveAdapter(ma);
            EventBusUtils.post(me);

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
            Msg clone = SerializationUtils.clone(msg);
            ComputationalCostsEvent cce = new ComputationalCostsEvent(jid, clone);
            EventBusUtils.post(cce);
        }

    }

    @Override
    public boolean dispaly(String jid, String from) {
        // 是否加密
        boolean flag = true;
        // 总开关
        String key1 = XMPPUtil.jidToName(jid) + MM;
        String statue1 = jedisService.getValue(key1);

        logger.info("总开关---key:{}----statue1:{}", key1, statue1);
        if ("1".equals(statue1)) {
            flag = false;
        } else {
            // 坐席是否欠费
            String status2 = jedisService.getValue(jid);
            logger.info("坐席Jid {} ------ statue2:{}", jid, status2);
            if ("1".equals(status2)) {
                String key3 = XMPPUtil.removeRoomSource(from);
                String status3 = jedisService.getValue(key3);
                logger.info("好友或者群 {} ------ status3:{}", key3, status3);
                if ("1".equals(status3)) {
                    flag = false;
                }
            }
        }

        return !flag;
    }

}
