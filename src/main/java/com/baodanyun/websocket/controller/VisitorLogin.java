package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.service.*;
import com.baodanyun.websocket.util.XMPPUtil;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yutao on 2016/10/4.
 */
@RestController
@RequestMapping("visitorlogin")
public class VisitorLogin extends BaseController {
    private static final String LOGIN_USER = "u";
    private static final String LOGIN_TO = "t";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserServer userServer;

    @Autowired
    private XmppService xmppService;

    @Autowired
    private XmppUserOnlineServer xmppUserOnlineServer;

    @Autowired
    @Qualifier("wvUserLifeCycleService")
    private UserLifeCycleService userLifeCycleService;

    @Autowired
    private UserCacheServer userCacheServer;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView visitor(HttpServletRequest request, HttpServletResponse response) throws IOException, XMPPException, SmackException {
        ModelAndView mv = new ModelAndView();
        String base = request.getContextPath();
        String accessId = request.getParameter(LOGIN_USER);
        String to = request.getParameter(LOGIN_TO);
        logger.info("accessId:[" + accessId + "] ------ContextPath[" + base + "]");

        try {

            Visitor visitor = userServer.initVisitor(base, accessId, to);
            request.getSession().setAttribute(Common.USER_KEY, visitor);
            boolean flag = customerOnline(visitor.getCustomer().getId());
            if (flag) {
                if (!xmppService.isAuthenticated(visitor.getId())) {
                    userLifeCycleService.login(visitor);
                }
                mv = getOnline(visitor, visitor.getCustomer().getId());

            } else {
                //客服不在线
                mv = getOffline(visitor, visitor.getCustomer().getId());
            }


        } catch (Exception e) {
            mv.addObject("statue", false);
            mv.addObject("customerIsOnline", false);
            mv.addObject("msg", e.getMessage());
            mv.setViewName("/visitor");
        }
        return mv;
    }

    public ModelAndView getOnline(Visitor visitor, String customerJid) throws BusinessException, InterruptedException, XMPPException, SmackException, IOException {
        // 客服在线
        ModelAndView mv = new ModelAndView();

        mv.addObject("statue", true);
        mv.addObject("customerIsOnline", true);
        mv.addObject("visitor", visitor);
        mv.addObject("customerJid", customerJid);
        mv.setViewName("/visitor");

        return mv;
    }


    public ModelAndView getOffline(Visitor visitor, String customerJid) throws BusinessException {
        ModelAndView mv = new ModelAndView();

        if (null == visitor) {
            visitor = userServer.initVisitor();
        }
        mv.addObject("statue", false);
        mv.addObject("visitor", visitor);
        mv.addObject("customerJid", customerJid);
        mv.addObject("customerIsOnline", false);
        mv.setViewName("/visitor");

        return mv;
    }

    public boolean customerOnline(String cid) {
        boolean flag = xmppService.isAuthenticated(cid);
        if (!flag) {
            flag = xmppUserOnlineServer.isOnline(XMPPUtil.jidToName(cid));
        }

        return flag;
    }
}
