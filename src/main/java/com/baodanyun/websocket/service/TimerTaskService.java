package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.apiBean.ExpandMsg;
import com.baodanyun.websocket.event.UCMessageEvent;
import com.baodanyun.websocket.util.Config;
import com.baodanyun.websocket.util.EventBusUtils;
import com.baodanyun.websocket.util.HttpUtils;
import com.baodanyun.websocket.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2016/11/15.
 */
@Service
public class TimerTaskService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private boolean dealMsgSendControl = true;
    @Autowired
    private LastVisitorSendMessageService las;

    @Autowired
    private XmppService xmppService;


    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private MsgConsumer msgConsumer;


    /**
     * 消息超时处理
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    //需要注意@Scheduled这个注解，它可配置多个属性：cron\fixedDelay\fixedRate
    public void test() {
        try {
            las.search();
        } catch (Exception e) {
            logger.error("操作失败", e);
        }
    }


    /**
     * 消息超时处理
     */
    @Scheduled(cron = "0/5 * * * * ?")
    //需要注意@Scheduled这个注解，它可配置多个属性：cron\fixedDelay\fixedRate
    public void get() {
        try {
            String json = HttpUtils.get("http://" + Config.xmppurl + ":9090/getExpandMsg");
            ExpandMsg em = JSONUtil.toObject(ExpandMsg.class, json);
            UCMessageEvent ue = new UCMessageEvent();
            ue.setExpandMsg(em);
            EventBusUtils.post(ue);
        } catch (Exception e) {
            logger.error("操作失败", e);
        }
    }


    /**
     * 启动消息处理线程
     */
    @Scheduled(fixedRate = 1000 * 60 * 60 * 24 * 365)
    public void dealMsgSendControl() {
        if (dealMsgSendControl) {
            Thread th  = new Thread(msgConsumer);

            th.start();

            dealMsgSendControl = false;
        }

    }

}