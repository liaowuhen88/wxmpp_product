package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.request.AppKeyVisitorLoginBean;
import com.baodanyun.websocket.bean.response.AppKeyResponse;
import com.baodanyun.websocket.bean.user.AppCustomer;
import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.exception.BusinessException;

/**
 * Created by liaowuhen on 2017/6/30.
 */
public interface AppKeyService {

    AppKeyResponse getAppKeyResponse(AppKeyVisitorLoginBean re) throws Exception;

    /**
     * 根据appkey获取绑定用户信息
     *
     * @param re
     * @return
     */
    AppCustomer getCustomerByAppKey(AppKeyResponse re, String url) throws BusinessException;

    Visitor getVisitor(AppKeyVisitorLoginBean re, AppKeyResponse ar, String uname, String token) throws BusinessException;

    Visitor getVisitorByToken(String token) throws BusinessException;
}
