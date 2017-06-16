package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.Customer;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.LoginModel;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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

    protected static Logger logger = Logger.getLogger(CustomerApi.class);

    @RequestMapping(value = "customerLogin")
    public ModelAndView customerLogin(LoginModel user, HttpServletRequest request, HttpServletResponse response) {
        //客服必须填写用户名 和 密码
        logger.info("user" + JSONUtil.toJson(user));
        ModelAndView mv = new ModelAndView();
        try {
            AbstractUser customer = customerInit(user);
            request.getSession().setAttribute(Common.USER_KEY, customer);
            mv.addObject("user", JSONUtil.toJson(customer));
            mv.setViewName("/customer/chat");
        } catch (BusinessException e) {
            mv.setViewName("/index");
            mv.addObject("msg", e.getMessage());
        }
        return mv;
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

        customer.setLoginUsername(user.getUsername());
        customer.setId(XMPPUtil.nameToJid(user.getUsername()));

        return customer;
    }

}
