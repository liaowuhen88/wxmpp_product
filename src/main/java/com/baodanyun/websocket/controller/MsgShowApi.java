package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.request.MsgShowBean;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.service.externalInterface.MsgShowService;
import com.baodanyun.websocket.util.AccessControlAllowUtils;
import com.baodanyun.websocket.util.Render;
import com.baodanyun.websocket.util.XMPPUtil;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liaowuhen on 2017/8/2.
 */
@RestController
public class MsgShowApi extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MsgShowService msgShowService;

    @RequestMapping(value = "msgShow", method = {RequestMethod.GET, RequestMethod.POST})
    public void visitor(MsgShowBean re, HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException, XMPPException, SmackException {
        Response response;
        try {
            String appkey = (String) request.getSession().getAttribute(Common.APPKEY);
            response = msgShowService.update(appkey, "updatemsgshow", re);

        } catch (Exception e) {
            logger.error("", e);
            response = new Response();
            response.setSuccess(false);
            response.setMsg(e.getMessage());
        }

        AccessControlAllowUtils.access(httpServletResponse);
        Render.r(httpServletResponse, XMPPUtil.buildJson(response));
    }

}
