package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.Tags;
import com.baodanyun.websocket.bean.user.AbstractUser;
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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yutao on 2016/10/10.
 */
@RestController
public class VisitorApi extends BaseController {
    protected static Logger logger = Logger.getLogger(CustomerApi.class);

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
     * @param vjid
     * @param httpServletResponse
     */
    @RequestMapping(value = "visitorOff/{vjid}")
    public void visitorOff(@PathVariable("vjid") String vjid, HttpServletRequest request,HttpServletResponse httpServletResponse) {
        Response response = new Response();
        try {
            AbstractUser cu = (AbstractUser) request.getSession().getAttribute(Common.USER_KEY);
            Visitor user = new Visitor();
            user.setId(vjid);
            user.setCustomer(cu);
            //userCacheServer.delete(CommonConfig.USER_ONLINE,cu.getId(),user);

            //userLifeCycleService.logout(user);


            response.setSuccess(true);

        } catch (Exception e) {
            logger.error(e);
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }

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
            logger.error(e);
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
                Visitor vCard = vcardService.getBaseVCard(Common.userVcard, id, customer.getId(), Visitor.class);
                Map<String, Object> map = new HashMap<>();
                map.put("vCard", vCard);

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
            logger.error(e);
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
            logger.error(e);
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
                Visitor vCard = vcardService.getBaseVCard(Common.userVcard, user.getCjid(), Visitor.class);

                if (!StringUtils.isEmpty(user.getDesc())) {
                    vCard.setDesc(user.getDesc().trim());
                }

                if (!StringUtils.isEmpty(user.getTags())) {
                    List<Tags> li = JSONUtil.toObject(List.class, user.getTags());
                    if (!CollectionUtils.isEmpty(li)) {
                        vCard.setTags(li);
                    }
                }

                if (!StringUtils.isEmpty(user.getRemark())) {
                    vCard.setRemark(user.getRemark().trim());
                }
                Visitor u = vcardService.updateBaseVCard(user.getCjid(), Common.userVcard, vCard);
                response.setData(u);
                response.setSuccess(true);
            }


        } catch (Exception e) {
            logger.error(e);
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }


}
