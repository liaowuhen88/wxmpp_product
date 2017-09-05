package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.request.AppKeyVisitorLoginBean;
import com.baodanyun.websocket.bean.response.AppKeyResponse;
import com.baodanyun.websocket.bean.user.AppCustomer;
import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.service.AppKeyService;
import com.baodanyun.websocket.util.Config;
import com.baodanyun.websocket.util.HttpUtils;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liaowuhen on 2017/6/30.
 */
@Service
public class AppKeyServiceImpl implements AppKeyService {
    protected static Logger logger = LoggerFactory.getLogger(AppKeyServiceImpl.class);

    private static Map<String, Visitor> visitors = new ConcurrentHashMap<>();
    private String appKeyUrl = Config.appKeyUrl;

    /*public static void main(String[] args) throws IOException {
        Map query = new HashMap();
        query.put("action","getwebzx");
        query.put("content","1");
        query.put("appkey","30faa867d0b0d0c609c4fd94fc3aef4d");

            String result= HttpUtils.get("http://xv.wifigx.com/api/webinit",query);
            logger.info(result);
    }*/

    @Override
    public AppKeyResponse getAppKeyResponse(AppKeyVisitorLoginBean re) throws Exception {
        if (StringUtils.isEmpty(re.getAppKey())) {
            throw new BusinessException("appKey is null");
        }

        Map query = new HashMap();
        query.put("action", "getwebzx");
        query.put("appkey", re.getAppKey());
        query.put("content", JSONUtil.toJson(re));

        String result = HttpUtils.get(appKeyUrl, query);
        logger.info(result);
        if (StringUtils.isEmpty(result)) {
            throw new BusinessException("result is error");
        }
        Response response = JSONUtil.toObject(Response.class, result);
        if (null == response.getData()) {
            throw new BusinessException("appkey is error");
        }

        Map mapappKey = (Map) response.getData();

        return JSONUtil.toObject(AppKeyResponse.class, JSONUtil.toJson(mapappKey));

    }

    /**
     * 如果校验出错，抛异常
     *
     * @param re
     * @return
     */
    @Override
    public AppCustomer getCustomerByAppKey(AppKeyResponse re, String url) throws BusinessException {

        AppCustomer au = new AppCustomer();
        au.setId(XMPPUtil.nameToJid(re.getcName()));
        au.setLoginUsername(re.getcName());
        au.setOpenId(re.getcName());
        au.setUserName(re.getNickName());
        au.setIcon(re.getIcon());
        au.setCustomerIsOnline(true);
        au.setSocketUrl(url + "/sockjs/newVisitor");
        au.setOssUrl(url + "/api/fileUpload/" + re.getcName());
        au.setToken(UUID.randomUUID().toString());
        au.setLoginTime(System.currentTimeMillis());
        au.setSkin(re.getSkin());
        au.setWebsite_icon(re.getWebsite_icon());
        return au;

    }

    @Override
    public Visitor getVisitor(AppKeyVisitorLoginBean re, AppKeyResponse ar, String uname, String token) throws BusinessException {
        String loginName = uname + "-web";

        if (StringUtils.isEmpty(re.getId())) {
            throw new BusinessException("id is null");
        }

        if (StringUtils.isEmpty(uname)) {
            throw new BusinessException("uname is null");
        }
        Visitor au = new Visitor();

        au.setLoginUsername(loginName);
        au.setLoginTime(System.currentTimeMillis());
        au.setOpenId(loginName);
        au.setUserName(re.getId());
        au.setNickName(ar.getName());
        au.setIcon(ar.getWebsite_icon());
        au.setPassWord("00818863ff056f1d66c8427836f94a87");
        au.setAgency(true);
        au.setId(au.getAgencyFrom());
        visitors.put(token, au);
        return au;
    }

    @Override
    public Visitor getVisitorByToken(String token) throws BusinessException {
        if (StringUtils.isEmpty(token)) {
            throw new BusinessException("token is null");
        }
        return visitors.get(token);
    }


}
