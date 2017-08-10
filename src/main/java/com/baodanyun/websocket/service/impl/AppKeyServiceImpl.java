package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.request.AppKeyVisitorLoginBean;
import com.baodanyun.websocket.bean.response.AppKeyCustomer;
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

import java.io.IOException;
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

    /**
     * 如果校验出错，抛异常
     *
     * @param appkey
     * @return
     */
    @Override
    public AppCustomer getCustomerByAppKey(String appkey, String url) throws BusinessException {

        if (StringUtils.isEmpty(appkey)) {
            throw new BusinessException("appKey is null");
        }

        Map query = new HashMap();
        query.put("action", "getwebzx");
        query.put("content", "1");
        query.put("appkey", appkey);

        try {
            String result = HttpUtils.get(appKeyUrl, query);
            logger.info(result);
            if(StringUtils.isEmpty(result)){
                throw new BusinessException("result is error");
            }
            Response response = JSONUtil.toObject(Response.class, result);
            if (null == response.getData()) {
                throw new BusinessException("appkey is error");
            }

            Map mapappKey = (Map) response.getData();
            AppKeyCustomer appKeyCustomer = JSONUtil.toObject(AppKeyCustomer.class, JSONUtil.toJson(mapappKey));
            AppCustomer au = new AppCustomer();
            au.setId(XMPPUtil.nameToJid(appKeyCustomer.getcName()));
            au.setLoginUsername(appKeyCustomer.getcName());
            au.setOpenId(appKeyCustomer.getcName());
            au.setUserName(appKeyCustomer.getNickName());
            au.setIcon(appKeyCustomer.getIcon());
            au.setCustomerIsOnline(true);
            au.setSocketUrl(url + "/sockjs/newVisitor");
            au.setOssUrl(url + "/api/fileUpload/" + appKeyCustomer.getcName());
            au.setToken(UUID.randomUUID().toString());
            au.setLoginTime(System.currentTimeMillis());
            return au;

        } catch (IOException e) {
            logger.error("error", e);
            new BusinessException("请求接口异常");
        } catch (Exception e) {
            logger.error("error", e);
            new BusinessException("解析异常");
        }
        return null;
    }

    @Override
    public Visitor getVisitor(AppKeyVisitorLoginBean re, String token) throws BusinessException {
        if (StringUtils.isEmpty(re.getId())) {
            throw new BusinessException("id is null");
        }
        Visitor au = new Visitor();
        au.setId(XMPPUtil.nameToJid(re.getId()));
        au.setLoginUsername(re.getId());
        au.setLoginTime(System.currentTimeMillis());
        au.setOpenId(re.getId());
        au.setUserName(re.getNickname());
        au.setIcon(re.getAvatar());
        if (StringUtils.isNotEmpty(re.getNickname())) {
            au.setNickName(re.getNickname() + "[pc网站]");
        } else {
            au.setNickName("[pc网站]");
        }

        au.setPassWord("00818863ff056f1d66c8427836f94a87");

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
