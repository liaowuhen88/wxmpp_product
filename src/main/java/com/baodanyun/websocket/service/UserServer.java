package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.Customer;
import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.bean.userInterface.user.PersonalInfo;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.Transferlog;
import com.baodanyun.websocket.service.impl.PersonalServiceImpl;
import com.baodanyun.websocket.util.CommonConfig;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liaowuhen on 2016/11/11.
 * <p/>
 * 管理在线用户的统一服务类
 */
@Service
public class UserServer {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PersonalServiceImpl personalService;

    @Autowired
    private XmppService xmppService;

    @Autowired
    private UserCacheServer userCacheServer;

    @Autowired
    @Qualifier("wvUserLifeCycleService")
    private UserLifeCycleService wvuserLifeCycleService;

    /**
     * 保存登陆成功的XMppConnection
     *
     * @param userId
     * @param user
     */

    public void saveUser(String userId, AbstractUser user) {

        if (user instanceof Customer) {
            getCustomers().put(userId, user);
        } else if (user instanceof Visitor) {
            getVisitors().put(userId, user);
        }
    }

    /**
     * 获取登录成功的XMPPConnection
     *
     * @param userId
     * @return
     */

    public Visitor getUserVisitor(String userId) {
        Visitor user = (Visitor) getVisitors().get(userId);
        return user;
    }

    public Customer getUserCustomer(String userId) {
        Customer user = (Customer) getCustomers().get(userId);
        return user;
    }

    public AbstractUser getUser(String userId) {
        AbstractUser user = getVisitors().get(userId);
        if (null != user) {
            return user;
        } else {
            return getCustomers().get(userId);
        }
    }

    public Visitor InitUserByOpenId(String contextPath, String openId) {

        Visitor visitor = new Visitor();
        visitor.setOpenId(openId);

        PersonalInfo pe = null;
        try {
            pe = personalService.getPersonalInfo(openId);
        } catch (BusinessException e) {
            logger.error("personalService.getPersonalInfo error", e);
        }
        Map account = null;
        try {
            account = personalService.getPersonalUserAccount(openId);
            visitor.setIcon(account.get("icon").toString());
        } catch (Exception e) {
            visitor.setIcon(contextPath + "/resouces/images/user_photo_default.jpg");
        }

        logger.info("getPersonalInfo:--->" + JSONUtil.toJson(pe));
        logger.info("getPersonalUserAccount:---->" + JSONUtil.toJson(account));

        if (null != pe) {
            visitor.setLoginUsername(pe.getMobile());
            visitor.setUserName(pe.getMobile());
            visitor.setNickName(pe.getPname());
            visitor.setId(XMPPUtil.nameToJid(pe.getMobile()));
            visitor.setUid(pe.getUseraccountid());

        } else {
            visitor.setLoginUsername(openId.toLowerCase());
            visitor.setUserName(openId.toLowerCase());
            visitor.setNickName("游客");
            visitor.setId(XMPPUtil.nameToJid(openId.toLowerCase()));
        }


        return visitor;
    }

    /**
     * 第三方接口初始化用户信息
     *
     * @param contextPath  项目部署根目录
     * @param accessId     openid 微信唯一标识符
     * @return
     * @throws BusinessException
     */
    public Visitor initVisitor(String contextPath, String accessId,String to) throws BusinessException {
        if (StringUtils.isBlank(accessId)) {
            throw new BusinessException("accessId 参数不能为空");
        }  else {
            String pwd = "00818863ff056f1d66c8427836f94a87";
            Visitor visitor = this.InitUserByOpenId(contextPath, accessId);
            AbstractUser customer;
            if(StringUtils.isEmpty(to)){
                customer= userCacheServer.getVisitorCustomer(visitor.getOpenId());
            }else {
                customer=getUserCustomer(XMPPUtil.nameToJid(to));
            }

            visitor.setCustomer(customer);
            visitor.setPassWord(pwd);
            visitor.setLoginTime(new Date().getTime());
            return visitor;
        }
    }

    public Visitor initVisitor() throws BusinessException {

        Visitor visitor = new Visitor();
        visitor.setLoginTime(new Date().getTime());
        return visitor;

    }

    public void removeUser(AbstractUser user) {
        getVisitors().remove(user);
        getCustomers().remove(user);
    }

    public Map<String, AbstractUser> getCustomers() {
        Object ob = userCacheServer.get(CommonConfig.USER_CUSTOMER);
        if(null != ob){
            return (HashMap<String,AbstractUser>)ob;
        }else {
            return new HashMap<>();
        }
    }

    public Map<String, AbstractUser> getVisitors() {
        Object ob = userCacheServer.get(CommonConfig.USER_VISITOR);
        if(null != ob){
            return (HashMap<String,AbstractUser>)ob;
        }else {
            return new HashMap<>();
        }
    }

    public synchronized boolean changeVisitorTo(Transferlog tm) throws BusinessException, InterruptedException {

        logger.info("Transferlog----->" + JSONUtil.toJson(tm));

        if (StringUtils.isEmpty(tm.getVisitorjid())) {
            throw new BusinessException("手机端用户id为空");
        }
        if (StringUtils.isEmpty(tm.getTransferto())) {
            throw new BusinessException("被转接客服账号id为空");
        }

        if (StringUtils.isEmpty(tm.getTransferfrom())) {
            throw new BusinessException("转出客服账号id为空");
        }

        boolean toflag = xmppService.isAuthenticated(tm.getTransferto());
        Customer foc = getUserCustomer(tm.getTransferfrom());
        Customer toc = getUserCustomer(tm.getTransferto());
        Visitor visitor = getUserVisitor(tm.getVisitorjid());
        if (toflag) {
            if (!wvuserLifeCycleService.uninstallVisitor(visitor)) {
                throw new BusinessException("从当前客服卸载失败");
            }
            if (!wvuserLifeCycleService.joinQueue(visitor)) {
                throw new BusinessException("加入到目标客服失败");
            }
        } else {
            throw new BusinessException("转出客服已经下线");
        }

        return true;
    }
}
