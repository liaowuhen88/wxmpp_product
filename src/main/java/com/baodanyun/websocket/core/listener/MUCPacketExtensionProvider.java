package com.baodanyun.websocket.core.listener;


/**
 * Created by liaowuhen on 2017/5/11.
 */


import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.event.JoinRoomEvent;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.MsgService;
import com.baodanyun.websocket.service.XmppService;
import com.baodanyun.websocket.util.EventBusUtils;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class MUCPacketExtensionProvider extends IQProvider {

    private static Logger logger = Logger.getLogger(MUCPacketExtensionProvider.class);
    private MsgSendControl msgSendControl;
    private XmppService xmppService;
    private MsgService msgService;
    private AbstractUser user;

    public MUCPacketExtensionProvider(XmppService xmppService,MsgService msgService, MsgSendControl msgSendControl, AbstractUser user) {
        this.xmppService = xmppService;
        this.msgSendControl = msgSendControl;
        this.user = user;
        this.msgService = msgService;
    }

    @Override
    public Element parse(XmlPullParser parser, int i) throws XmlPullParserException, IOException, SmackException {
        int eventType = parser.getEventType();
        Set<String> rooms = new HashSet<>();
        while (true) {
            if (eventType == XmlPullParser.START_TAG) {
                if ("room".equals(parser.getName())) {
                    String account = parser.getAttributeValue("", "account");
                    String room = parser.nextText();
                    try {
                        rooms.add(room);
                    } catch (Exception e) {
                       logger.error(e);
                    }

                    logger.info("account is " + account + " and room is " + room);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if ("muc".equals(parser.getName())) {
                    break;
                }
            }
            eventType = parser.next();
        }

        try {
            JoinRoomEvent je = new JoinRoomEvent(user,rooms);
            EventBusUtils.post(je);
        } catch (Exception e) {
           logger.error(e);
        }
        return null;
    }


}
