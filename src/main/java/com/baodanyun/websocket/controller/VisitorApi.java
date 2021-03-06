package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.user.Customer;
import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.bean.userInterface.PersonalDetail;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.model.UserModel;
import com.baodanyun.websocket.service.*;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.Render;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yutao on 2016/10/10.
 */
@RestController
public class VisitorApi extends BaseController {
    protected static Logger logger = LoggerFactory.getLogger(CustomerApi.class);

    @Autowired
    private PersonalService personalService;

    @Autowired
    private VcardService vcardService;

    @Autowired
    private UserServer userServer;

    @Autowired
    private UserCacheServer userCacheServer;

    @Autowired
    @Qualifier("wvUserLifeCycleService")
    private UserLifeCycleService userLifeCycleService;

    /**
     * 获取访客节点信息
     *
     * @param vjid
     * @param httpServletResponse
     */
    @RequestMapping(value = "visitor/{vjid}", method = RequestMethod.GET)
    public void visitor(@PathVariable("vjid") String vjid, HttpServletResponse httpServletResponse) {
        Response response = new Response();
        try {
            Visitor user = userServer.getUserVisitor(vjid);
            if (null != user) {
                response.setSuccess(true);
                response.setData(user);
            } else {
                response.setSuccess(false);
            }
        } catch (Exception e) {
            logger.error("error", e);
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }

    /**
     * 获取当前访客详情
     *
     * @param httpServletResponse
     */
    @RequestMapping(value = "visitorDetail")
    public void visitorDetail(String openid, String id, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Response response = new Response();

        Customer customer = (Customer) request.getSession().getAttribute(Common.USER_KEY);

        try {
            if (!StringUtils.isEmpty(id)) {
                Map<String, Object> map = new HashMap<>();

                if (!StringUtils.isEmpty(openid)) {
                    try {
                        PersonalDetail personalDetail = personalService.getPersonalDetail(openid);
                        map.put("basic", personalDetail);
                    } catch (Exception e) {
                        logger.error("第三方接口获取数据出出错", e);
                        response.setMsg("第三方接口获取数据出出错");
                    }
                } else {
                    response.setMsg("openid不能为空");
                }

                response.setData(map);
                response.setSuccess(true);
            } else {
                response.setSuccess(false);
                response.setMsg("用户id不能为空");
            }
        } catch (Exception e) {
            logger.error("error", e);
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }


    /**
     * 获取当前访客详情
     *
     * @param httpServletResponse
     */

    @RequestMapping(value = "changeCustomerJid")
    public void changeCustomerJid(String openId, String cJid, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Response response = new Response();
        try {
            if (!StringUtils.isEmpty(openId) && !StringUtils.isEmpty(cJid)) {
                userCacheServer.addVisitorCustomerOpenId(openId, XMPPUtil.nameToJid(cJid));
                response.setSuccess(true);
            } else {
                response.setSuccess(false);
                response.setMsg("用户id不能为空");
            }
        } catch (Exception e) {
            logger.error("error", e);
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }

    /**
     * 修改昵称等信息打标签等
     *
     * @param
     * @param httpServletResponse
     */

    @RequestMapping(value = "upVisitorInfo", method = RequestMethod.POST)
    public void upVisitorInfo(UserModel user, HttpServletResponse httpServletResponse) {
        Response response = new Response();

        try {
            if (StringUtils.isEmpty(user.getCjid())) {
                response.setSuccess(false);
                response.setMsg("参数cjid不能为空");
            } else {
                response.setSuccess(true);
            }


        } catch (Exception e) {
            logger.error("error", e);
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }


}
