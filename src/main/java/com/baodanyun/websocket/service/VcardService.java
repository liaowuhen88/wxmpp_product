package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.user.Customer;
import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.exception.BusinessException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by liaowuhen on 2016/11/25.
 */
@Service
public class VcardService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private XmppService xmppService;


    /**
     * 获取Vcard
     *
     * @param xmppid
     * @param Jid
     * @return
     */
    public VCard loadVcard(String xmppid, String Jid) {
        VCard vcard = null;
        try {
            vcard = VCardManager.getInstanceFor(xmppService.getXMPPConnectionAuthenticated(xmppid)).loadVCard(Jid);
        } catch (Exception e) {
            logger.error("", e);
        }
        return vcard;
    }


    /**
     * 获取客服信息
     *
     * @param
     * @return
     */

    public <T> T getBaseVCard(String key, String jid, Class<T> c) {
        VCard vcard = null;
        try {
            vcard = loadVCard(jid);
        } catch (Exception e) {
            logger.error("getBaseVCarderror", e);
        }


        return getBaseVCard(key, vcard, c);
    }


    public void InitCustomer(Customer user) {
        Customer vCard = this.getBaseVCard(Common.userVcard, user.getId(), Customer.class);
        if (null != vCard) {
            user.setIcon(vCard.getIcon());

        } else {
            user.setLoginTime(new Date().getTime());
            this.updateBaseVCard(user.getId(), Common.userVcard, user);
        }
    }

    public void InitVisitor(Visitor visitor) {
        Visitor vCard = this.getBaseVCard(Common.userVcard, visitor.getId(), Visitor.class);
        if (null != vCard) {
            visitor.setDesc(vCard.getDesc());
            visitor.setTags(vCard.getTags());
            this.updateBaseVCard(visitor.getId(), Common.userVcard, visitor);
        } else {
            visitor.setLoginTime(new Date().getTime());
            this.updateBaseVCard(visitor.getId(), Common.userVcard, visitor);
        }
    }



    /**
     * 获取客服信息
     *
     * @param
     * @return
     */

    public <T> T getBaseVCard(String key, String jid, String xmppId, Class<T> c) {

        VCard vcard = null;
        try {
            vcard = loadVCard(xmppId, jid);
        } catch (Exception e) {
            logger.error("getBaseVCarderror", e);
        }
        return getBaseVCard(key, vcard, c);

    }


    /**
     * 获取客服信息
     *
     * @param
     * @return
     */

    public <T> T getBaseVCard(String key, VCard vcard, Class<T> c) {
       /* try {

            if (vcard == null) {
                return null;
            } else {
                String js = vcard.getField(key);
                logger.info(js);
                return JSONUtil.toObject(c,js );
            }

        } catch (Exception e) {
            logger.error("update vcard error", e);
        }*/
        return null;
    }


    /**
     * 更新VCard
     *
     * @param jid
     * @param key
     * @param cu
     * @param <T>
     * @return
     */
    public <T> T updateBaseVCard(String jid, String key, T cu) {
       /* try {
            VCard vcard = loadVCard(jid);
            if (vcard == null) {
                vcard = new VCard();
            }
            vcard.setField(key, JSONUtil.toJson(cu));
            if (cu instanceof AbstractUser) {
                AbstractUser au = (AbstractUser) cu;
                vcard.setNickName(au.getNickName());
            }

            saveVCard(jid, vcard);
            return cu;


        } catch (Exception e) {
            logger.error("update vcard error", e);
        }*/
        return null;
    }


    //获取对应的vcard值
    public String getVCardValueByKey(String jid, String key) {
        try {

            VCard vcard = loadVCard(jid);

            return vcard.getField(key) == null ? "" : vcard.getField(key);


        } catch (Exception e) {
            logger.error("getVCardValueByKey  error");
        }
        return "";
    }


    public VCard loadVCard(String jid) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException, BusinessException {
       /* if (xmppServer.isAuthenticated(jid)) {
            VCardManager vm = VCardManager.getInstanceFor(xmppServer.getXMPPConnectionAuthenticated(jid));
            if (vm != null) {
                return vm.loadVCard(jid);
            }
        }*/
        return null;
    }

    public VCard loadVCard(String xmppId, String jid) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException, BusinessException {
       /* if (xmppServer.isAuthenticated(xmppId)) {
            VCardManager vm = VCardManager.getInstanceFor(xmppServer.getXMPPConnectionAuthenticated(xmppId));
            if (vm != null) {
                return vm.loadVCard(jid);
            }
        }*/
        return null;
    }

    public void saveVCard(String jid, VCard vcard) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException, BusinessException {
      /*  if (xmppServer.isAuthenticated(jid)) {
            VCardManager vm = VCardManager.getInstanceFor(xmppServer.getXMPPConnectionAuthenticated(jid));
            if (vm != null) {
                vm.saveVCard(vcard);
            }
        }*/
    }


    /**
     * 修改密码
     *
     * @return
     */

    public boolean changePassword(String jid, String pwd) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException, BusinessException {
        XMPPConnection xmppConnection = xmppService.getXMPPConnectionAuthenticated(jid);
        if (xmppConnection != null) {

            AccountManager.getInstance(xmppConnection).changePassword(pwd);
            return true;
        } else {
            throw new BusinessException("xmppConnection is not Authenticated");
        }
    }
}
