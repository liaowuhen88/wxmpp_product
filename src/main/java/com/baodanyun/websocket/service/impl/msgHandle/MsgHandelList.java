package com.baodanyun.websocket.service.impl.msgHandle;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.service.MsgHandleService;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2017/9/12.
 */

@Service
public class MsgHandelList {
    protected static Logger logger = LoggerFactory.getLogger(MsgHandelList.class);
    @Autowired
    @Qualifier(value = "h5ToUMsgHandle")
    private MsgHandleService h5ToUMsgHandle;

    @Autowired
    @Qualifier(value = "h5ToCMsgHandle")
    private MsgHandleService h5ToCMsgHandle;

    @Autowired
    @Qualifier(value = "sysMsgHandle")
    private MsgHandleService sysMsgHandle;

    @Autowired
    @Qualifier(value = "psMsgHandle")
    private MsgHandleService psMsgHandle;
    @Autowired
    @Qualifier(value = "wxMsgHandle")
    private MsgHandleService wxMsgHandle;


    public boolean handel(AbstractUser user, Message msg, Msg sendMsg) {
        boolean flag = false;
        if (sysMsgHandle.canHandel(msg, sendMsg)) {
            flag = true;
            logger.info("sysMsgHandle");
            sysMsgHandle.handel(user, msg, sendMsg);
        } else if (h5ToUMsgHandle.canHandel(msg, sendMsg)) {
            flag = true;
            logger.info("h5ToUMsgHandle");
            h5ToUMsgHandle.handel(user, msg, sendMsg);
        } else if (h5ToCMsgHandle.canHandel(msg, sendMsg)) {
            flag = true;
            logger.info("h5ToCMsgHandle");
            h5ToCMsgHandle.handel(user, msg, sendMsg);
        } else if (psMsgHandle.canHandel(msg, sendMsg)) {
            flag = true;
            logger.info("psMsgHandle");
            psMsgHandle.handel(user, msg, sendMsg);
        } else if (wxMsgHandle.canHandel(msg, sendMsg)) {
            flag = true;
            logger.info("wxMsgHandle");
            wxMsgHandle.handel(user, msg, sendMsg);
        }
        return flag;

    }


}
