package com.baodanyun.websocket.listener.impl;


import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.event.ComputationalCostsEvent;
import com.baodanyun.websocket.listener.EventBusListener;
import com.baodanyun.websocket.service.JedisService;
import com.baodanyun.websocket.util.JSONUtil;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2017/5/12.
 * <p/>
 * 通知客户端有新的会话
 */

@Service
public class ComputationalCostsEventBusListenerImpl extends AbstarctEventBusListener<ComputationalCostsEvent> implements EventBusListener<ComputationalCostsEvent> {

    private static Logger logger = LoggerFactory.getLogger(ComputationalCostsEventBusListenerImpl.class);

    @Autowired
    private JedisService jedisService;


    @Override
    @Subscribe
    public boolean processExpiringEvent(final ComputationalCostsEvent computationalCostsEvent) {
        logger.info(computationalCostsEvent.toString());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Msg clone = computationalCostsEvent.getCloneMsg();
                    if (Msg.Type.msg.toString().equals(clone.getType())) {
                        jedisService.product(getKey(), JSONUtil.toJson(clone));
                    } else {
                        logger.info("type status not computationalCosts");
                    }

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

}
