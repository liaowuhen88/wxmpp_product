package com.baodanyun.websocket.util;

import com.baodanyun.websocket.core.common.Common;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yutao on 2016/7/12.
 */
public class XMPPUtil {
    public static Logger logger = LoggerFactory.getLogger(XMPPUtil.class);

    public static String nameToJid(String name) {
        return name + Common.COMMON_XMPP_DOMAIN;
    }

    public static String jidToName(String jid) {
        return jid.substring(0, jid.indexOf(Common.COMMON_XMPP_DOMAIN_CHAR));
    }

    public static String removeSource(String jid) {
        if (StringUtils.isNotBlank(jid) && jid.contains("/")) {
            return jid.substring(0, jid.lastIndexOf("/"));
        }
        return jid;
    }

    public static String removeRoomSource(String jid) {
        if (StringUtils.isNotBlank(jid) && jid.contains("/")) {
            return jid.substring(0, jid.indexOf("/"));
        }
        return jid;
    }

    public static boolean isRoom(String jid) {
        if (StringUtils.isNotBlank(jid) && jid.contains("@conference")) {
            return true;
        }
        return false;
    }

    public static String getRoomName(String room) {
        if (StringUtils.isNotBlank(room) && room.contains("@")) {
            return room.substring(0, room.indexOf("@"));
        }
        return room;
    }

    public static String buildJson(Object o) {
       return JSONUtil.toJson(o);
    }

}
