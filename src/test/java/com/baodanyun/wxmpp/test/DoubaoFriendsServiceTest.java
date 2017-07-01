package com.baodanyun.wxmpp.test;

//import com.baodanyun.websocket.service.order.QDOrderHelper;

import com.baodanyun.websocket.bean.pageSearch.OfMessagearchiveSearchPage;
import com.baodanyun.websocket.model.DoubaoFriends;
import com.baodanyun.websocket.model.OfMessagearchiveWithBLOBs;
import com.baodanyun.websocket.service.DoubaoFriendsService;
import com.baodanyun.websocket.service.OfMessagearchiveService;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by 峥桂 on 2016/11/15.
 */
public class DoubaoFriendsServiceTest extends BaseTest {

    @Autowired
    private DoubaoFriendsService doubaoFriendsService;

    @Autowired
    private OfMessagearchiveService ofMessagearchiveService;


    @Test
    public void desc() {
        List<DoubaoFriends> li = doubaoFriendsService.selectByJid("zwv11132@126xmpp");

        System.out.print(li.size());

    }

    @Test
    public void select() {
        OfMessagearchiveSearchPage page = new OfMessagearchiveSearchPage();
        page.setJid("xvql1127@conference.126xmpp");

        PageInfo<OfMessagearchiveWithBLOBs> pageInfo = ofMessagearchiveService.select(page);

        for (OfMessagearchiveWithBLOBs m : pageInfo.getList()) {
            System.out.println(m.getMessageid());
        }


    }
}
