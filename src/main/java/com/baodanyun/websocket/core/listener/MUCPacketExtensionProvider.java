package com.baodanyun.websocket.core.listener;


/**
 * Created by liaowuhen on 2017/5/11.
 */


import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.service.MsgSendControl;
import com.baodanyun.websocket.service.MsgService;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


public class MUCPacketExtensionProvider extends IQProvider {

    private static Logger logger = Logger.getLogger(MUCPacketExtensionProvider.class);
    private MsgSendControl msgSendControl;
    private MsgService msgService;

    private AbstractUser user;

    public MUCPacketExtensionProvider(MsgService msgService, MsgSendControl msgSendControl, AbstractUser user) {
        this.msgSendControl = msgSendControl;
        this.user = user;
        this.msgService = msgService;
    }

    @Override
    public Element parse(XmlPullParser parser, int i) throws XmlPullParserException, IOException, SmackException {
        int eventType = parser.getEventType();
        while (true) {
            if (eventType == XmlPullParser.START_TAG) {
                if ("room".equals(parser.getName())) {
                    String account = parser.getAttributeValue("", "account");
                    String room = parser.nextText();
                    try {
                        Msg msg = msgService.getNewRoomJoines(room, user.getId());
                        msgSendControl.sendMsg(msg);
                    } catch (InterruptedException e) {
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
        return null;
    }
}
