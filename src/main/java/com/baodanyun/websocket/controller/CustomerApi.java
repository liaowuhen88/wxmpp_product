package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.UserSetPW;
import com.baodanyun.websocket.bean.msg.ConversationMsg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.model.*;
import com.baodanyun.websocket.service.*;
import com.baodanyun.websocket.service.impl.JedisServiceImpl;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.Render;
import com.baodanyun.websocket.util.XMPPUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yutao on 2016/10/10.
 */
@RestController
public class CustomerApi extends BaseController {

    protected static Logger logger = LoggerFactory.getLogger(CustomerApi.class);

    @Autowired
    private TransferServer transferServer;

    @Autowired
    private VcardService vcardService;

    @Autowired
    private UserServer userServer;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private OfmucroomService ofmucroomService;

    @Autowired
    @Qualifier("wcUserLifeCycleService")
    private UserLifeCycleService userLifeCycleService;

    @Autowired
    private JedisService jedisService;

    @Autowired
    private MsgService msgService;

    @Autowired
    private MessageFiterService messageFiterService;

    @Autowired
    private FriendAndGroupService friendAndGroupService;


    /**
     * 获取客服的信息
     *
     * @param cjid
     * @param httpServletResponse
     */

    @RequestMapping(value = "customer/{cjid}", method = RequestMethod.GET)
    public void customer(@PathVariable("cjid") String cjid, HttpServletRequest request, HttpServletResponse httpServletResponse) {
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

    @RequestMapping(value = "getVcard")
    public void getVcard(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Response response = new Response();
        try {
            AbstractUser user = (AbstractUser) request.getSession().getAttribute(Common.USER_KEY);
            Ofvcard vCard = vcardService.loadVcard(XMPPUtil.jidToName(user.getId()));
            ConversationMsg sm = new ConversationMsg();
            msgService.initByVCard(sm, vCard);
            response.setData(sm);
            response.setSuccess(true);
        } catch (Exception e) {
            logger.error("", e);
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
    public void upCustomerInfo(UserModel user, HttpServletRequest request, HttpServletResponse httpServletResponse) {
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
            logger.error("", e);
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
    public void upCustomerPwd(UserSetPW pw, HttpServletRequest request, HttpServletResponse httpServletResponse) {
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
            logger.error("error", e);
            response.setSuccess(false);
            response.setMsg("系统异常");
        }

        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }

    /**
     * 关闭访客
     *
     * @param vjid
     * @param httpServletResponse
     */
    @RequestMapping(value = "visitorOff")
    public void visitorOff(String vjid, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Response response = new Response();
        try {
            AbstractUser cu = (AbstractUser) request.getSession().getAttribute(Common.USER_KEY);
            Visitor user = new Visitor();
            user.setId(vjid);
            user.setCustomer(cu);
            conversationService.removeConversations(cu.getId(), vjid);
            response.setSuccess(true);

        } catch (Exception e) {
            logger.error("error", e);
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }

    /**
     * @param httpServletResponse
     */

    @RequestMapping(value = "visitorList")
    public void visitorList(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Response response = new Response();
        try {
            AbstractUser cu = (AbstractUser) request.getSession().getAttribute(Common.USER_KEY);

            Map map = conversationService.get(cu.getId());

            response.setData(map.keySet());
            response.setSuccess(true);

        } catch (Exception e) {
            logger.error("error", e);
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }

    /**
     * 获取消息是否显示
     *
     * @param httpServletResponse
     */

    @RequestMapping(value = "getDisplay")
    public void getDisplay(String from, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Response response = new Response();
        try {
            AbstractUser cu = (AbstractUser) request.getSession().getAttribute(Common.USER_KEY);
            boolean isEncrypt = messageFiterService.isEncrypt(cu.getId(), from);
            String status = "1";
            if (isEncrypt) {
                status = "0";
            }

            String count = jedisService.getFromMap(JedisServiceImpl.ENCRYPTCOUNT, from);
            Map<String, String> map = new HashMap<>();
            if (StringUtils.isEmpty(count) || "null".equals(count)) {
                count = "0";
            }
            map.put("count", count);
            map.put("status", status);

            response.setData(map);
            response.setSuccess(true);

        } catch (Exception e) {
            logger.error("error", e);
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }

    /**
     * @param httpServletResponse
     */

    @RequestMapping(value = "getConversation")
    public void getConversation(String from, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Response response = new Response();
        try {
            AbstractUser cu = (AbstractUser) request.getSession().getAttribute(Common.USER_KEY);
            String json = conversationService.get(cu.getId(), from);
            if (StringUtils.isNotEmpty(json)) {
                ConversationMsg cm = JSONUtil.toObject(ConversationMsg.class, json);
                boolean isEncrypt = messageFiterService.isEncrypt(cu.getId(), from);
                logger.info("isEncrypt {}", isEncrypt);
                if (isEncrypt) {
                    cm.setOnlineStatus(ConversationMsg.OnlineStatus.encrypt);
                } else {
                    cm.setOnlineStatus(ConversationMsg.OnlineStatus.online);
                }
                response.setData(cm);
                response.setSuccess(true);
            } else {
                ConversationMsg conversation;
                if (XMPPUtil.isRoom(from)) {
                    Ofmucroom ofmucroom = ofmucroomService.selectByPrimaryKey((long) 1, XMPPUtil.getRoomName(from));
                    conversation = msgService.getNewRoomJoines(from, ofmucroom, cu.getId(), cu.getAppkey(), cu);
                } else {
                    conversation = msgService.getNewPersionalJoines(from, cu);
                }
                response.setData(conversation);
                response.setSuccess(true);
            }

        } catch (Exception e) {
            logger.error("error", e);
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }
    /**
     * @param httpServletResponse
     */

    /*@RequestMapping(value = "getGroupUsers")
    public void getGroupUsers(String from, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        Response response = new Response();
        try {
            AbstractUser cu = (AbstractUser) request.getSession().getAttribute(Common.USER_KEY);
            List<GroupUser> list = friendAndGroupService.getGroupUsers(cu.getAppkey(), XMPPUtil.jidToName(from));
            response.setSuccess(true);
            response.setData(list);
        } catch (Exception e) {
            logger.error("error", e);
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }*/



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
            mv.addObject("msg", "您已退出");
        } catch (Exception e) {
            logger.error("error", e);
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
            logger.error("error", e);
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
           /* Collection<AbstractUser> freeCustomerNodeList = userServer.getCustomers().values();
            response.setData(freeCustomerNodeList);*/
            response.setSuccess(true);
        } catch (Exception e) {
            logger.error("error", e);
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
            logger.error("error", e);
            response.setMsg(e.getMessage());
            response.setSuccess(false);
        }
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }

}
