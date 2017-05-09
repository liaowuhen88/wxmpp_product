package com.baodanyun.wxmpp.test;

//import com.baodanyun.websocket.service.order.QDOrderHelper;

import com.baodanyun.websocket.model.DoubaoFriends;
import com.baodanyun.websocket.service.DoubaoFriendsService;
import com.baodanyun.websocket.util.CommonConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by 峥桂 on 2016/11/15.
 */
public class DoubaoFriendsServiceTest extends BaseTest{

    @Autowired
    private DoubaoFriendsService doubaoFriendsService;
    @Test
    public  void desc() {
        List<DoubaoFriends> li= doubaoFriendsService.selectByJid("zwv11132@126xmpp");

        System.out.print(li.size());

    }


}
