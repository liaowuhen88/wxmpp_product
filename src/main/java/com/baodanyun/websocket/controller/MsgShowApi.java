package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.pageSearch.MessagearchiveAdapterSearchPage;
import com.baodanyun.websocket.bean.request.MaterialPageBean;
import com.baodanyun.websocket.bean.request.MsgShowBean;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.model.MessageArchiveAdapter;
import com.baodanyun.websocket.service.MessageArchiveAdapterService;
import com.baodanyun.websocket.service.externalInterface.MsgShowService;
import com.baodanyun.websocket.util.AccessControlAllowUtils;
import com.baodanyun.websocket.util.Render;
import com.baodanyun.websocket.util.XMPPUtil;
import com.github.pagehelper.PageInfo;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liaowuhen on 2017/8/2.
 */
@RestController
public class MsgShowApi extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MsgShowService msgShowService;
    @Autowired
    private MessageArchiveAdapterService messageArchiveAdapterService;


    @RequestMapping(value = "msgShow", method = {RequestMethod.GET, RequestMethod.POST})
    public void visitor(MsgShowBean re, HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException, XMPPException, SmackException {
        Response response;
        try {
            AbstractUser cu = (AbstractUser) request.getSession().getAttribute(Common.USER_KEY);
            String appkey = (String) request.getSession().getAttribute(Common.APPKEY);
            response = msgShowService.update(appkey, "updatemsgshow", re);
            if (response.isSuccess() && re.getStatus().equals("1")) {
                MessagearchiveAdapterSearchPage page = new MessagearchiveAdapterSearchPage();
                page.setPageSize(re.getCount());
                page.setToJid(cu.getId());
                page.setFromJid(re.getUsername());

                PageInfo<MessageArchiveAdapter> li = messageArchiveAdapterService.selectByFromAndTo(page);

                Map<String, Object> map = new HashMap<>();
                map.put("msg", li);
                response.setData(map);
            }
        } catch (Exception e) {
            logger.error("", e);
            response = new Response();
            response.setSuccess(false);
            response.setMsg(e.getMessage());
        }

        AccessControlAllowUtils.access(httpServletResponse);
        Render.r(httpServletResponse, XMPPUtil.buildJson(response));
    }

    /**
     * 获取第三方素材
     *
     * @param re
     * @param request
     * @param httpServletResponse
     * @throws IOException
     * @throws XMPPException
     * @throws SmackException
     */
    @RequestMapping(value = "getMaterial", method = {RequestMethod.GET, RequestMethod.POST})
    public void getMaterial(MaterialPageBean re, HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException, XMPPException, SmackException {
        Response response;
        try {
            String appkey = (String) request.getSession().getAttribute(Common.APPKEY);

            response = msgShowService.getMaterial(appkey, re);

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
