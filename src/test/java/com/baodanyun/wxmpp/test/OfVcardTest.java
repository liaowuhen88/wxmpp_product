package com.baodanyun.wxmpp.test;

import com.baodanyun.websocket.model.Ofvcard;
import com.baodanyun.websocket.util.JSONUtil;
import org.jdom.JDOMException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by liaowuhen on 2017/9/11.
 */
public class OfVcardTest {
    protected static final Logger logger = LoggerFactory.getLogger(OfVcardTest.class);

    @Test
    public void analysisVcard() throws JDOMException, IOException {
        Ofvcard of = new Ofvcard();
        Ofvcard.User user = of.getUser();

        logger.info("fn " + user.getFn());
        logger.info(JSONUtil.toJson(user));


    }
}
