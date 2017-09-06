package com.baodanyun.websocket.core.listener;

import com.baodanyun.websocket.util.JSONUtil;
import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.packet.Presence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liaowuhen on 2017/9/6.
 */
public class InitPresenceListener implements PresenceListener {
    private static Logger logger = LoggerFactory.getLogger(InitPresenceListener.class);

    @Override
    public void processPresence(Presence presence) {
        logger.info(JSONUtil.toJson(presence));
    }
}
