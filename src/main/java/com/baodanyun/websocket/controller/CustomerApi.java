package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.UserSetPW;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.model.PageModel;
import com.baodanyun.websocket.model.Transferlog;
import com.baodanyun.websocket.model.UserModel;
import com.baodanyun.websocket.service.TransferServer;
import com.baodanyun.websocket.service.UserLifeCycleService;
import com.baodanyun.websocket.service.UserServer;
import com.baodanyun.websocket.service.VcardService;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.Render;
import com.baodanyun.websocket.util.XMPPUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * Created by yutao on 2016/10/10.
 */
@RestController
public class CustomerApi extends BaseController {

    protected static Logger logger = Logger.getLogger(CustomerApi.class);


    @Autowired
    private TransferServer transferServer;

    @Autowired
    private VcardService vcardService;

    @Autowired
    private UserServer userServer;

    @Autowired
    @Qualifier("wcUserLifeCycleService")
    private UserLifeCycleService userLifeCycleService;

    /**
     * 获取客服的信息
     *
     * @param cjid
     * @param httpServletResponse
     */

    @RequestMapping(value = "customer/{cjid}", method = RequestMethod.GET)
    public void customer(@PathVariable("cjid") String cjid,HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Response response = new Response();
        try {
            AbstractUser user = userServer.getCustomers().get(cjid);
            if (user != null) {
                response.setData(user);
                response.setSuccess(true);
            } else {
                response.setSuccess(false);
                response.setMsg("客服不存在");
            }
        } catch (Exception e) {
            logger.error("获取客服错误", e);
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }

    /**
     * 修改昵称等信息
     *
     * @param
     * @param httpServletResponse
     */

    @RequestMapping(value = "upCustomerInfo", method = RequestMethod.POST)
    public void upCustomerInfo(UserModel user, HttpServletRequest request,HttpServletResponse httpServletResponse) {
        Response response = new Response();
        AbstractUser cu = userServer.getCustomers().get(user.getCjid());
        try {
            if (StringUtils.isEmpty(user.getCjid())) {
                response.setSuccess(false);
                response.setMsg("用户id不能为空");
            } else {

                if (!StringUtils.isEmpty(user.getDesc())) {
                    cu.setDesc(user.getDesc());
                }

                if (!StringUtils.isEmpty(user.getNickName())) {
                    cu.setNickName(user.getNickName());
                }
                if (!StringUtils.isEmpty(user.getIcon())) {
                    cu.setIcon(user.getIcon());
                }

                if (!StringUtils.isEmpty(user.getUserName())) {
                    cu.setUserName(user.getUserName());
                }

                //AbstractUser u = vcardService.updateBaseVCard(cu.getId(), Common.userVcard, cu);

               // response.setData(u);
                response.setSuccess(true);
            }


        } catch (Exception e) {
            logger.error(e);
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }

    /**
     * 修改客服密码
     *
     * @param
     * @param httpServletResponse
     */

    @RequestMapping(value = "upCustomerPwd", method = RequestMethod.POST)
    public void upCustomerPwd(UserSetPW pw, HttpServletRequest request,HttpServletResponse httpServletResponse) {
        Response response = new Response();
        AbstractUser cu = userServer.getCustomers().get(pw.getCjid());

        try {

 
            if (!StringUtils.isEmpty(pw.getNewPWD())) {
                if (pw.getNewPWD().trim().equals(pw.getConfirmPWD())) {
                    if (cu.getPassWord().equals(pw.getOldPWD())) {
                        boolean flag = vcardService.changePassword(XMPPUtil.jidToName(pw.getCjid()), pw.getNewPWD().trim());
                        if (flag) {
                            response.setMsg("密码修改成功");
                            response.setSuccess(flag);
                        } else {
                            response.setMsg("密码修改失败");
                            response.setSuccess(flag);
                        }

                    } else {
                        response.setSuccess(false);
                        response.setMsg("密码错误");
                    }
                } else {
                    response.setSuccess(false);
                    response.setMsg("您两次输入的密码不一致");
                }
            } else {
                response.setSuccess(false);
                response.setMsg("新密码不能为空");
            }

        } catch (Exception e) {
            logger.error(e);
            response.setSuccess(false);
            response.setMsg("系统异常");
        }

        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }



    /**
     * 客服退出
     *
     * @param httpServletRequest
     * @param httpServletResponse
     */
    @RequestMapping(value = "customerLogout")
    public ModelAndView customerLogout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        ModelAndView mv = new ModelAndView();
        try {
            logger.info("customerLogout");
            // 清楚session缓存
            AbstractUser customer = (AbstractUser) httpServletRequest.getSession().getAttribute(Common.USER_KEY);
            httpServletRequest.getSession().invalidate();
            // 关闭node
            userLifeCycleService.logout(customer);

            mv.setViewName("/index");
            mv.addObject("msg","您已退出");
        } catch (Exception e) {
            logger.error(e);
        }

        return mv;
    }

    /**
     * 获取其他线上队列为
     *
     * @param httpServletResponse
     */
    @RequestMapping(value = "freeCustomerList")
    public void freeCustomerList(HttpServletResponse httpServletResponse) {
        Response response = new Response();
        Gson gson = new Gson();
        try {
            Collection<AbstractUser> freeCustomerNodeList = userServer.getCustomers().values();
            response.setData(gson.toJson(freeCustomerNodeList));
            response.setSuccess(true);
        } catch (Exception e) {
            logger.error(e);
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, gson.toJson(response));
    }

    /**
     * 获取在线的客服节点列表
     *
     * @param httpServletResponse
     */

    @RequestMapping(value = "onlineCustomerList")
    public void onlineCustomerList(PageModel model, HttpServletResponse httpServletResponse) {
        Response response = new Response();
        try {
            Collection<AbstractUser> freeCustomerNodeList = userServer.getCustomers().values();
            response.setData(freeCustomerNodeList);
            response.setSuccess(true);
        } catch (Exception e) {
            logger.error(e);
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }

    /**
     * 转接客服
     *
     * @param
     */
    @RequestMapping(value = "changeVisitorTo")
    public void changeVisitorTo(String vjid, String fromJid, String toJid, HttpServletResponse httpServletResponse) {
        Response response = new Response();
        try {
            Transferlog tm = new Transferlog();
            tm.setTransferto(toJid);
            tm.setTransferfrom(fromJid);
            tm.setVisitorjid(vjid);
            tm.setCause("客服主动转接");

            boolean flag = transferServer.changeVisitorTo(tm);
            response.setSuccess(flag);
        } catch (Exception e) {
            logger.error(e);
            response.setMsg(e.getMessage());
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }

}
