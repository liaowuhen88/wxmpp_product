package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.user.Customer;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.service.DoubaoFriendsService;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.Render;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yutao on 2016/10/10.
 */
@RestController
public class CloseFriendWindowApi extends BaseController {

    protected static Logger logger = Logger.getLogger(CloseFriendWindowApi.class);

    @Autowired
    private DoubaoFriendsService doubaoFriendsService;

    /**
     * @param httpServletResponse
     */
    @RequestMapping(value = "closeFriendWindow", method = RequestMethod.POST)
    public void keepOnline(String jid, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Customer customer = (Customer) request.getSession().getAttribute(Common.USER_KEY);
        doubaoFriendsService.deleteByFriendJname(customer.getId(),jid);
        Response response = new Response();
        response.setSuccess(true);

        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }


}
