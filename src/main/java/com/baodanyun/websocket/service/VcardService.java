package com.baodanyun.websocket.service;

import com.baodanyun.websocket.dao.OfvcardMapper;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.Ofvcard;
import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2016/11/25.
 */
@Service
public class VcardService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private XmppService xmppService;
    @Autowired
    private OfvcardMapper ofvcardMapper;


    public Ofvcard loadVcard(String username) throws BusinessException {
        if (StringUtils.isEmpty(username)) {
            throw new BusinessException("username can not be null");
        }
        return ofvcardMapper.selectByPrimaryKey(username);
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
