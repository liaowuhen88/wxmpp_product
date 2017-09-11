package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.Customer;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.LoginModel;
import com.baodanyun.websocket.service.OfuserService;
import com.baodanyun.websocket.util.AccessControlAllowUtils;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.Render;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yutao on 2016/10/4.
 */
@RestController
public class CustomerLogin extends BaseController {

    protected static Logger logger = LoggerFactory.getLogger(CustomerApi.class);

    @Autowired
    private OfuserService ofuserService;

    @RequestMapping(value = "customerLogin")
    public ModelAndView customerLogin(LoginModel user, HttpServletRequest request, HttpServletResponse response) {
        //客服必须填写用户名 和 密码
        logger.info("user" + JSONUtil.toJson(user));
        ModelAndView mv = new ModelAndView();
        try {
            AbstractUser customer = customerInit(user);
            ofuserService.checkOfUser(customer.getLoginUsername(), customer.getPassWord());
            request.getSession().setAttribute(Common.USER_KEY, customer);
            request.getSession().setAttribute(Common.APPKEY, user.getAppkey());
            mv.addObject("user", JSONUtil.toJson(customer));
            mv.setViewName("/customer/chat");
        } catch (BusinessException e) {
            mv.setViewName("/index");
            mv.addObject("msg", e.getMessage());
        }
        return mv;
    }

    @RequestMapping(value = "mobileCustomerLogin")
    public void mobileCustomerLogin(LoginModel user, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        //客服必须填写用户名 和 密码
        logger.info("user" + JSONUtil.toJson(user));
        Response response = new Response();
        boolean flag = false;
        try {
            AbstractUser customer = customerInit(user);
            flag = ofuserService.checkOfUser(customer.getLoginUsername(), customer.getPassWord());
            request.getSession().setAttribute(Common.USER_KEY, customer);

            response.setData(customer);
            response.setSuccess(flag);

        } catch (BusinessException e) {
            response.setSuccess(flag);
            response.setMsg(e.getMessage());
        }
        AccessControlAllowUtils.access(httpServletResponse);
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }





    public Customer customerInit(LoginModel user) throws BusinessException {
        Customer customer = new Customer();
        if (StringUtils.isBlank(user.getUsername())) {
            throw new BusinessException("用户名密码不能为空");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            customer.setPassWord("111111");
        } else {
            customer.setPassWord(user.getPassword());
        }

        customer.setUserName(user.getUsername());
        customer.setLoginUsername(user.getUsername());
        customer.setId(XMPPUtil.nameToJid(user.getUsername()));
        customer.setAppkey(user.getAppkey());

        return customer;
    }

}
