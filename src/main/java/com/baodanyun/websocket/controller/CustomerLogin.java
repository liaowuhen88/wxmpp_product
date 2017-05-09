package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.Customer;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.LoginModel;
import com.baodanyun.websocket.service.UserLifeCycleService;
import com.baodanyun.websocket.service.UserServer;
import com.baodanyun.websocket.service.VcardService;
import com.baodanyun.websocket.service.XmppService;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.Render;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yutao on 2016/10/4.
 */
@RestController
public class CustomerLogin extends BaseController {

    @Autowired
    private VcardService vcardService;

    @Autowired
    private XmppService xmppService;

    @Autowired
    private UserServer userServer;


    @Autowired
    @Qualifier("wcUserLifeCycleService")
    private UserLifeCycleService userLifeCycleService;

    protected static Logger logger = Logger.getLogger(CustomerApi.class);

    @RequestMapping(value = "loginApi", method = RequestMethod.POST)
    public void api(LoginModel user, HttpServletRequest request, HttpServletResponse response) {
        //客服必须填写用户名 和 密码
        AbstractUser cu = (AbstractUser) request.getSession().getAttribute(Common.USER_KEY);
        try {
            if (null == cu || !xmppService.isAuthenticated(cu.getId())) {
                cu = customerLogin(user);
                request.getSession().setAttribute(Common.USER_KEY, cu);
            } else {
                logger.info("jid[" + cu.getId() + "] is login");
            }
        } catch (BusinessException e) {
            cu = null;
        }

        Render.r(response, XMPPUtil.buildJson(getRespone(cu)));

    }

    @RequestMapping(value = "customerLogin")
    public ModelAndView customerLogin(LoginModel user, HttpServletRequest request, HttpServletResponse response) {
        //客服必须填写用户名 和 密码
        logger.info("user"+JSONUtil.toJson(user));
        ModelAndView mv = new ModelAndView();
        AbstractUser customer = (AbstractUser) request.getSession().getAttribute(Common.USER_KEY);
        try {
            if (null == customer || !xmppService.isAuthenticated(customer.getId())) {
                customer = customerLogin(user);
                request.getSession().setAttribute(Common.USER_KEY, customer);
            } else {
                logger.info("jid[" + customer.getId() + "] is login");
            }
            mv.addObject("user",JSONUtil.toJson(customer));
            mv.setViewName("/customer/chat");
        } catch (BusinessException e) {
            mv.setViewName("/index");
            mv.addObject("msg",e.getMessage());
        }
        return mv;

    }

    public Response getRespone(AbstractUser cu) {
        Response responseMsg = new Response();
        if (null == cu) {
            responseMsg.setMsg(Common.ErrorCode.LOGIN_ERROR.getCodeName());
            responseMsg.setCode(Common.ErrorCode.LOGIN_ERROR.getCode());
            responseMsg.setSuccess(false);
        } else {
            responseMsg.setSuccess(true);
        }
        return responseMsg;
    }


    public Customer customerLogin(LoginModel user) throws BusinessException {
        Customer customer = new Customer();
        if (StringUtils.isBlank(user.getUsername())) {
            throw  new BusinessException("用户名密码不能为空");
        }
        if(StringUtils.isBlank(user.getPassword())){
            customer.setPassWord("111111");
        }else {
            customer.setPassWord(user.getPassword());
        }

        customer.setLoginUsername(user.getUsername());
        customer.setId(XMPPUtil.nameToJid(user.getUsername()));
        try {
            if (userLifeCycleService.login(customer)) {
                //vcardService.InitCustomer(customer);
                return customer;
            }else {
                throw new BusinessException("");
            }

        } catch (Exception e) {
            logger.error("", e);
            throw new BusinessException("登录失败");
        }
    }
}
