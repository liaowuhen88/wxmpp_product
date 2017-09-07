package com.baodanyun.wxmpp.test;

import com.baodanyun.websocket.dao.OfconparticipantMapper;
import com.baodanyun.websocket.util.JSONUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by liaowuhen on 2017/9/7.
 */
public class OfconparticipantTest extends BaseTest {

    @Autowired
    private OfconparticipantMapper ofconparticipantMapper;

    @Test
    public void desc() throws Exception {

        Long time = ofconparticipantMapper.getLeftdate("xvsh7@126xmpp");
        System.out.println(JSONUtil.toJson(time));

    }
}
