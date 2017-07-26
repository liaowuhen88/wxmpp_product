package com.baodanyun.wxmpp.test;

import com.baodanyun.websocket.model.MessageArchiveAdapter;
import com.baodanyun.websocket.service.MessageArchiveAdapterService;
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


}
