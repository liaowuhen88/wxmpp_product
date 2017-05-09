package com.baodanyun.websocket.service;

import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.util.Config;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by liaowuhen on 2016/11/11.
 */
@Service
public class UserCacheServer {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CacheService cacheService ;

    @Autowired
    private UserServer userServer ;

    /**
     * 维护发送消息目的地
     */
    public boolean addVisitorCustomerOpenId(String jid, String to){
        boolean flag = false;
        if(!StringUtils.isEmpty(jid) && !StringUtils.isEmpty(to)){
            Object map = cacheService.get(jid);

            if(null == map ){
                map = new HashMap<String,String>();
            }
            HashMap<String,String> ma = (HashMap<String,String>)map;
            ma.put(jid,to);
            flag = cacheService.setOneMonth(jid,ma);
        }
        return flag;
    }

    /**
     * 获取发送地址
     */

    private String getVisitorCustomerOpenId(String openId){

        if(!StringUtils.isEmpty(openId)){
            Object map = cacheService.get(openId);
            if(null != map ){
                HashMap<String,String> ma = (HashMap<String,String>)map;
                return ma.get(openId);
            }else {
                return XMPPUtil.nameToJid(Config.controlId);
            }
        }
        return null;
    }

    /**
     * 获取发送地址
     */

    public AbstractUser getVisitorCustomer(String openId){
        String cjid = getVisitorCustomerOpenId(openId);
        logger.info(cjid);
        if(!StringUtils.isEmpty(cjid)){
            return userServer.getUserCustomer(cjid);
        }
        return null;
    }

    public synchronized boolean add(String key,String cid,AbstractUser visitorUser){
        Object map = cacheService.get(key);
        boolean flag;
        if(null == map ){
            map = new HashMap<String,Set<AbstractUser>>();
        }

        HashMap<String,Set<AbstractUser>> ma = (HashMap<String,Set<AbstractUser>>)map;
        Set<AbstractUser> set = ma.get(cid);
        if(null == set){
            set = new HashSet<>();
            ma.put(cid,set);
        }
        set.remove(visitorUser);
        set.add(visitorUser);
        flag = cacheService.setOneMonth(key,ma);

        return flag ;
    }


    public synchronized boolean add(String key,AbstractUser visitorUser){
        Object map = cacheService.get(key);
        boolean flag;
        if(null == map ){
            map = new HashMap<String,Set<AbstractUser>>();
        }
        HashMap<String,AbstractUser> ma = (HashMap<String,AbstractUser>)map;

        ma.put(visitorUser.getId(),visitorUser);

        flag = cacheService.setOneMonth(key,ma);

        return flag ;
    }

    public synchronized boolean addOpenId(String key,AbstractUser visitorUser){
        Object map = cacheService.get(key);
        boolean flag;
        if(null == map ){
            map = new HashMap<String,Set<AbstractUser>>();
        }
        HashMap<String,AbstractUser> ma = (HashMap<String,AbstractUser>)map;

        ma.put(visitorUser.getOpenId(),visitorUser);

        flag = cacheService.setOneMonth(key,ma);

        return flag ;
    }

    public synchronized Set<AbstractUser> get(String key,String cid){
        Object map = cacheService.get(key);
        if(null != map){
            HashMap<String,Set<AbstractUser>> ma = (HashMap<String,Set<AbstractUser>>)map;
            Set<AbstractUser> set = ma.get(cid);
            if(null != set){
                return set;
            }
        }
       return null;
    }

    public Object get(String key){
        return  cacheService.get(key);
    }

    public synchronized  void delete(String key,String cid,AbstractUser visitorUser){
        Object map = cacheService.get(key);
        if(null != map){
            HashMap<String,Set<AbstractUser>> ma = (HashMap<String,Set<AbstractUser>>)map;
            if(null != ma){
                Set<AbstractUser> set = ma.get(cid);
                if(null != set){
                    set.remove(visitorUser);
                }
            }

        }

       cacheService.setOneMonth(key,map);
    }


}
