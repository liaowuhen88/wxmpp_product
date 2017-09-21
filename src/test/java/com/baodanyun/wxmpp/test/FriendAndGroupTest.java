package com.baodanyun.wxmpp.test;

import com.baodanyun.websocket.bean.bootstrap.Node;
import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.response.FriendAndGroupResponse;
import com.baodanyun.websocket.bean.user.GroupUser;
import com.baodanyun.websocket.bean.user.PublicUser;
import com.baodanyun.websocket.service.FriendAndGroupService;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by liaowuhen on 2017/8/31.
 */
public class FriendAndGroupTest extends BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(FriendAndGroupTest.class);

    private static String appKey = "xQPwDNa6B2A8G9jy3zprEJV3gnZ4vW5O";
    @Autowired
    private FriendAndGroupService friendAndGroupService;

    @Test
    public void desc() throws Exception {
        List<FriendAndGroupResponse> list = friendAndGroupService.get("46bfb9709ab760e727d8ffe1b2ffb59e", null);
        System.out.println(JSONUtil.toJson(list));
        List<Node> nodes = friendAndGroupService.adapter(list);
        System.out.println(JSONUtil.toJson(nodes));

    }

    @Test
    public void getPublicUser() throws Exception {
        PublicUser list = friendAndGroupService.getPublicUser("xQPwDNa6B2A8G9jy3zprEJV3gnZ4vW5O", "public_7");
        System.out.println(JSONUtil.toJson(list));


    }

    @Test
    public void getRobotJid() throws Exception {
        String list = friendAndGroupService.getRobotJid("46bfb9709ab760e727d8ffe1b2ffb59e", "xvql518");
        System.out.println(list);


    }

    @Test
    public void getGroupUsers() throws Exception {
        String room = "xvql1048@126xmpp";
        ConversationMsg sm = new ConversationMsg();
        List<GroupUser> list = friendAndGroupService.getGroupUsers(appKey, XMPPUtil.jidToName(room));
        if (null != list) {
            for (GroupUser gu : list) {
                sm.getGroupUserMap().put(XMPPUtil.jidToName(gu.getJid()), gu);
            }
        }

        logger.info(JSONUtil.toJson(sm.getGroupUserMap()));
    }

}
