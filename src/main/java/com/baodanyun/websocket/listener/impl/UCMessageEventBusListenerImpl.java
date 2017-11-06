package com.baodanyun.websocket.listener.impl;


import com.baodanyun.websocket.bean.apiBean.ExpandMsg;
import com.baodanyun.websocket.event.UCMessageEvent;
import com.baodanyun.websocket.listener.EventBusListener;
import com.baodanyun.websocket.service.JedisService;
import com.baodanyun.websocket.service.OfMessagearchiveService;
import com.baodanyun.websocket.util.DateUtils;
import com.baodanyun.websocket.util.JSONUtil;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by liaowuhen on 2017/5/12.
 * <p/>
 * 通知客户端有新的会话
 */

@Service
public class UCMessageEventBusListenerImpl extends AbstarctEventBusListener<UCMessageEvent> implements EventBusListener<UCMessageEvent> {

    private static String MESSAGE_TOTAL = "message_total";
    private static String EVERY_DAY_MESSAGE_COUNT = "every_day_message_count";
    private static Logger logger = LoggerFactory.getLogger(UCMessageEventBusListenerImpl.class);
    private Long count;
    private Long everyDatCount;
    private String everyDate;
    @Autowired
    private JedisService jedisService;

    @Autowired
    private OfMessagearchiveService ofMessagearchiveService;

    @Override
    @Subscribe
    public boolean processExpiringEvent(final UCMessageEvent uCMessageEvent) {
        logger.info(JSONUtil.toJson(uCMessageEvent));

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    increase(uCMessageEvent.getExpandMsg());

                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        });
        return false;
    }

    private String getKey() {
        return "computationalCosts";
    }

    public synchronized void increase(ExpandMsg expandMsg) {
        if (expandMsg.getCount() == 0) {
            return;
        }
        if (null == count) {
            count = ofMessagearchiveService.getGroupMessageCount(null);
        }
        count = count + expandMsg.getCount();
        jedisService.addValue(MESSAGE_TOTAL, count + "");

        String now = DateUtils.format(new Date(), DateUtils.DATE_SMALL_STR);

        if (null == everyDatCount || !now.equals(everyDate)) {
            everyDate = DateUtils.format(new Date(), DateUtils.DATE_SMALL_STR);
            everyDatCount = ofMessagearchiveService.getGroupMessageCount(everyDate);
        }
        everyDatCount = everyDatCount + expandMsg.getCount();
        jedisService.addMap(EVERY_DAY_MESSAGE_COUNT, everyDate, everyDatCount + "");

    }

}
