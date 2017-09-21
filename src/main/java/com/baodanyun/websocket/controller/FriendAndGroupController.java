package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.bootstrap.Node;
import com.baodanyun.websocket.bean.response.FriendAndGroupResponse;
import com.baodanyun.websocket.bean.user.Customer;
import com.baodanyun.websocket.bean.user.GroupUser;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.service.FriendAndGroupService;
import com.baodanyun.websocket.util.Render;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by liaowuhen on 2017/8/31.
 */
@RestController
public class FriendAndGroupController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private FriendAndGroupService friendAndGroupService;

    @RequestMapping("getFriendAndGroup")
    public void getFriendAndGroup(String nickName, HttpServletRequest request, HttpServletResponse response) throws IOException, XMPPException, SmackException {
        Response responseMsg = new Response();
        Customer cu = (Customer) request.getSession().getAttribute(Common.USER_KEY);
        try {
            List<FriendAndGroupResponse> list = friendAndGroupService.get(cu.getAppkey(), nickName);
            List<Node> node = friendAndGroupService.adapter(list);
            responseMsg.setSuccess(true);
            responseMsg.setData(node);
        } catch (Exception e) {
            responseMsg.setSuccess(false);
            responseMsg.setMsg(e.getMessage());
            logger.error("error", e);
        }

        Render.r(response, XMPPUtil.buildJson(responseMsg));
    }


    @RequestMapping("getGroupUsers")
    public void getGroupUsers(String username, HttpServletRequest request, HttpServletResponse response) throws IOException, XMPPException, SmackException {
        Response responseMsg = new Response();
        Customer cu = (Customer) request.getSession().getAttribute(Common.USER_KEY);
        try {
            if (StringUtils.isNotEmpty(username)) {
                if (username.contains("@")) {
                    username = XMPPUtil.jidToName(username);
                }
                List<GroupUser> list = friendAndGroupService.getGroupUsers(cu.getAppkey(), username);
                responseMsg.setSuccess(true);
                responseMsg.setData(list);
            } else {
                responseMsg.setSuccess(false);
                responseMsg.setData("username 参数不能为空");
            }

        } catch (Exception e) {
            responseMsg.setSuccess(false);
            responseMsg.setMsg(e.getMessage());
            logger.error("error", e);
        }

        Render.r(response, XMPPUtil.buildJson(responseMsg));
    }

}
