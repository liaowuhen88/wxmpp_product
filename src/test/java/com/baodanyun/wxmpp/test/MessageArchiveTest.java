package com.baodanyun.wxmpp.test;

import com.baodanyun.websocket.dao.OfMessagearchiveMapper;
import com.baodanyun.websocket.model.MessageArchiveAdapter;
import com.baodanyun.websocket.service.MessageArchiveAdapterService;
import com.baodanyun.websocket.service.OfMessagearchiveService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by liaowuhen on 2017/7/21.
 */
public class MessageArchiveTest extends BaseTest {

    protected static final Logger logger = LoggerFactory.getLogger(MessageArchiveTest.class);

    private String mid = "liaowuhen";
    @Autowired
    private MessageArchiveAdapterService messageArchiveAdapterService;

    @Autowired
    private OfMessagearchiveService ofMessagearchiveService;

    @Autowired
    private OfMessagearchiveMapper ofMessagearchiveMapper;


    @Test
    public void insert() {
        MessageArchiveAdapter ma = new MessageArchiveAdapter();
        ma.setMessageid(mid);
        ma.setContent("nice to meet you");
        messageArchiveAdapterService.insert(ma);
    }

    @Test
    public void selectByMessageId() {
        MessageArchiveAdapter ma = messageArchiveAdapterService.selectByMessageId(mid);
        assertEquals(mid, ma.getMessageid());
    }

    @Test
    public void deleteByMessageId() {
        messageArchiveAdapterService.deleteByMessageId(mid);
        MessageArchiveAdapter ma = messageArchiveAdapterService.selectByMessageId(mid);
        assertNull(ma);
    }

    @Test
    public void getGroupMessageCount() {
        Long count = ofMessagearchiveMapper.getGroupMessageCount(1502640000000L, 1502726399000L);
        System.out.println("count" + count);
        count = ofMessagearchiveService.getGroupMessageCount("2017-08-14");
        System.out.println("count" + count);
        count = ofMessagearchiveService.getGroupMessageCount(null);
        System.out.println("count" + count);

    }
}
