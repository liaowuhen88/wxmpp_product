package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.userInterface.Company;
import com.baodanyun.websocket.bean.userInterface.PersonalDetail;
import com.baodanyun.websocket.bean.userInterface.RequestBean;
import com.baodanyun.websocket.bean.userInterface.claims.ClaimsInfo;
import com.baodanyun.websocket.bean.userInterface.order.OrderInfo;
import com.baodanyun.websocket.bean.userInterface.policy.EnterpriseContractByUidBean;
import com.baodanyun.websocket.bean.userInterface.user.PersonalInfo;
import com.baodanyun.websocket.controller.CustomerApi;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.service.CacheService;
import com.baodanyun.websocket.service.PersonalService;
import com.baodanyun.websocket.util.CommonConfig;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.KdtApiClient;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by yutao on 2016/9/27.
 */
@Service
public class PersonalServiceImpl implements PersonalService {
    protected static Logger logger = Logger.getLogger(CustomerApi.class);
    @Autowired
    private CacheService cacheService ;

    @Override
    public List<PersonalInfo> getPersonalInfos(Long uid) {
        String userInfo = (String) cacheService.get(CommonConfig.USER_INFO_KEY + uid);
        if (StringUtils.isBlank(userInfo) && null != uid) {
            RequestBean requestBean = new RequestBean(uid);
            userInfo = KdtApiClient.postJson(KdtApiClient.APiMethods.getUserInfo.getValue(), requestBean);
            cacheService.setOneDay(CommonConfig.USER_INFO_KEY + uid, userInfo);
        }

        if (!StringUtils.isEmpty(userInfo)) {
            java.lang.reflect.Type userInfoType = new TypeToken<List<PersonalInfo>>() {
            }.getType();

            List<PersonalInfo> userinfoList = JSONUtil.fromJson(userInfo, userInfoType);
            return userinfoList;
        } else {
            return null;
        }


    }

    @Override
    public List<PersonalInfo> getPersonalInfos(String openId) throws BusinessException {
        Long uid = getUidByOpenId(openId);
        if (null != uid) {
            return getPersonalInfos(uid);
        } else {
            return null;
        }

    }

    @Override
    public PersonalInfo getPersonalInfo(Long uid) {
        List<PersonalInfo> infos = getPersonalInfos(uid);
        if (!CollectionUtils.isEmpty(infos)) {
            for (PersonalInfo personalInfo : infos) {
                //只拿个人信息
                if (1 == personalInfo.getIdentitytype()) {
                    return personalInfo;
                }
            }
        }
        return null;
    }

    @Override
    public Map getPersonalUserAccount(String openId) throws Exception {
        Long uid = getUidByOpenId(openId);
        if (null != uid) {
            return getPersonalUserAccount(uid);
        } else {
            return null;
        }
    }

    @Override
    public Map getPersonalUserAccount(Long uid) throws Exception {
        String userAccount = (String) cacheService.get(CommonConfig.USER_ACCOUNT_KEY + uid);
        if (StringUtils.isBlank(userAccount)) {
            RequestBean requestBean = new RequestBean(uid);
            userAccount = KdtApiClient.postJson(KdtApiClient.APiMethods.getUserAccount.getValue(), requestBean);
            cacheService.setOneDay(CommonConfig.USER_ACCOUNT_KEY + uid, userAccount);

        }

        if (!StringUtils.isEmpty(userAccount)) {
            return JSONUtil.toObject(Map.class, userAccount);
        } else {
            return null;
        }
    }

    @Override
    public PersonalInfo getPersonalInfo(String openId) throws BusinessException {
        Long uid = getUidByOpenId(openId);
        if (null != uid) {
            return getPersonalInfo(uid);
        } else {
            return null;
        }


    }

    @Override
    public List<OrderInfo> getOrderInfo(Long uid) {

        String orderInfo = (String) cacheService.get(CommonConfig.USER_ORDER_KEY + uid);
        if (StringUtils.isBlank(orderInfo)) {
            RequestBean requestBean = new RequestBean(uid);
            orderInfo = KdtApiClient.postJson(KdtApiClient.APiMethods.getOrderInfo.getValue(), requestBean);
            cacheService.setOneDay(CommonConfig.USER_ORDER_KEY + uid, orderInfo);

        }

        if (!StringUtils.isEmpty(orderInfo)) {
            java.lang.reflect.Type userInfoType = new TypeToken<List<OrderInfo>>() {
            }.getType();
            List<OrderInfo> orderInfos = JSONUtil.fromJson(orderInfo, userInfoType);
            return orderInfos;
        } else {
            return null;
        }

    }

    @Override
    public List<OrderInfo> getOrderInfo(String openId) throws BusinessException {
        Long uid = getUidByOpenId(openId);

        if (null != uid) {

            return getOrderInfo(uid);
        } else {
            return null;
        }


    }

    @Override
    public List<ClaimsInfo> getClaimsInfo(Long uid) {

        String claimsInfo = (String) cacheService.get(CommonConfig.USER_CLAIMS_KEY + uid);
        if (StringUtils.isBlank(claimsInfo)) {
            RequestBean requestBean = new RequestBean(uid);
            claimsInfo = KdtApiClient.postJson(KdtApiClient.APiMethods.getClaimsInfo.getValue(), requestBean);
            cacheService.setOneDay(CommonConfig.USER_CLAIMS_KEY + uid, claimsInfo);

        }

        if (!StringUtils.isEmpty(claimsInfo)) {
            java.lang.reflect.Type claimsInfoType = new TypeToken<List<ClaimsInfo>>() {
            }.getType();
            List<ClaimsInfo> claimsInfos = JSONUtil.fromJson(claimsInfo, claimsInfoType);

            return claimsInfos;
        } else {
            return null;
        }

    }

    @Override
    public List<ClaimsInfo> getClaimsInfo(String openId) throws BusinessException {
        Long uid = getUidByOpenId(openId);

        if (null != uid) {
            return getClaimsInfo(uid);
        } else {
            return null;
        }
    }

    @Override
    public List<EnterpriseContractByUidBean> getContractInfo(Long uid) {

        String contractInfo = (String) cacheService.get(CommonConfig.USER_CONTRACT_KEY + uid);
        if (StringUtils.isBlank(contractInfo)) {
            RequestBean requestBean = new RequestBean(uid);
            contractInfo = KdtApiClient.postJson(KdtApiClient.APiMethods.getContract.getValue(), requestBean);
            cacheService.setOneDay(CommonConfig.USER_CONTRACT_KEY + uid, contractInfo);

        }

        if (!StringUtils.isEmpty(contractInfo)) {
            java.lang.reflect.Type contractInfoType = new TypeToken<List<EnterpriseContractByUidBean>>() {
            }.getType();
            List<EnterpriseContractByUidBean> contractInfos = JSONUtil.fromJson(contractInfo, contractInfoType);

            return contractInfos;
        } else {
            return null;
        }

    }

    @Override
    public List<EnterpriseContractByUidBean> getContractInfo(String openId) throws BusinessException {

        Long uid = getUidByOpenId(openId);
        if (null != uid) {
            return getContractInfo(uid);
        } else {
            return null;
        }
    }

    @Override
    public PersonalDetail getPersonalDetail(String openId) throws BusinessException {
        Long uid = getUidByOpenId(openId);
        if (null != uid) {
            return getPersonalDetail(uid);
        } else {
            return null;
        }

    }

    @Override
    public PersonalDetail getPersonalDetail(Long uid) {
        PersonalDetail pd = new PersonalDetail();

        PersonalInfo personalInfo = getPersonalInfo(uid);
        List<Company> company = getCompany(uid);
        List<PersonalInfo> personalInfos = getPersonalInfos(uid);

        List<ClaimsInfo> claimsInfos = getClaimsInfo(uid);//理赔
        List<OrderInfo> orderInfos = getOrderInfo(uid);//体检订单
        List<EnterpriseContractByUidBean> contractInfos = getContractInfo(uid);//合同订单

        pd.setPersonalInfo(personalInfo);
        pd.setPersonalInfos(personalInfos);
        pd.setOrderInfos(orderInfos);
        pd.setClaimsInfos(claimsInfos);
        pd.setContractInfos(contractInfos);
        pd.setCompany(company);

        return pd;
    }

    @Override
    public List<Company> getCompany(Long uid) {
        String company = (String) cacheService.get(CommonConfig.USER_COMPANY_KEY + uid);
        if (StringUtils.isBlank(company)) {
            RequestBean requestBean = new RequestBean(uid);
            company = KdtApiClient.postJson(KdtApiClient.APiMethods.getUserCompany.getValue(),requestBean);
            cacheService.setOneDay(CommonConfig.USER_COMPANY_KEY + uid, company);
        }

        if (!StringUtils.isEmpty(company)) {
            java.lang.reflect.Type companyType = new TypeToken<List<Company>>() {
            }.getType();
            List<Company> commpanys = JSONUtil.fromJson(company, companyType);

            return commpanys;
        } else {
            return null;
        }
    }


    /**
     * 根据openid获取uid
     *
     * @param openId
     * @return
     */
    public Long getUidByOpenId(String openId) throws BusinessException {

        KdtApiClient kdtApiClient = new KdtApiClient();
        RequestBean requestBean = new RequestBean();
        requestBean.setOpenId(openId);

        String uid = (String) cacheService.get(CommonConfig.USER_OPENID_KEY + openId);
        logger.info("uid-------:" + uid);
        if (StringUtils.isBlank(uid)) {
            uid = kdtApiClient.postJson(KdtApiClient.APiMethods.getUidByOpenId.getValue(), requestBean);
            if(StringUtils.isEmpty(uid)){
                uid = "-1";
            }
            logger.info(uid+"--------"+openId);
            logger.info(cacheService.setOneMonth(CommonConfig.USER_OPENID_KEY + openId, uid));
        }

        if (uid.equals("-1")) {
            throw new BusinessException("uid is -1");
        } else {
            return Long.valueOf(uid);
        }

    }


}
