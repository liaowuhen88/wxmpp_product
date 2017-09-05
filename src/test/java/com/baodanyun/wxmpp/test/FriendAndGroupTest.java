package com.baodanyun.wxmpp.test;

import com.baodanyun.websocket.bean.bootstrap.Node;
import com.baodanyun.websocket.bean.response.FriendAndGroupResponse;
import com.baodanyun.websocket.service.FriendAndGroupService;
import com.baodanyun.websocket.util.JSONUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by liaowuhen on 2017/8/31.
 */
public class FriendAndGroupTest extends BaseTest {
    @Autowired
    private FriendAndGroupService friendAndGroupService;

    @Test
    public void desc() throws Exception {
        List<FriendAndGroupResponse> list = friendAndGroupService.get("46bfb9709ab760e727d8ffe1b2ffb59e", null);
        System.out.println(JSONUtil.toJson(list));
        List<Node> nodes = friendAndGroupService.adapter(list);
        System.out.println(JSONUtil.toJson(nodes));

    }

}
