package com.baodanyun.websocket.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by yutao on 2016/7/20.
 */
public class Config {
    public static final String enable_debug;
    public static final String xmppurl;
    public static final String xmppport;
    public static final String userinfointerface;
    public static final String xmppdomain;
    public static final String cacheurl;
    public static final String cacheport;
    public static final String appSecret;
    public static final String appKey;
    public static final String mallInfoInterface;
    public static final String upload_prefix;
    public static final String nev;
    public static final String weiXinCallback;
    public static final String controlId;
    public static final String privateBody;
    public static final String publicBody;
    public static final String appKeyUrl;
    public static final String sasUrl;



    static {
        Properties p = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = Config.class.getResourceAsStream("/spring-config.properties");
            p.load(inputStream);
            inputStream = Config.class.getResourceAsStream("/spring-oss.properties");
            p.load(inputStream);

        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO: 2016/10/8
                    e.printStackTrace();
                }
            }
        }
        enable_debug = p.getProperty("open.msg.debug");
        userinfointerface = p.getProperty("user.info.interface");
        mallInfoInterface = p.getProperty("mall.info.order");
        xmppurl = p.getProperty("xmpp.ip");
        xmppport = p.getProperty("xmpp.port");
        xmppdomain = p.getProperty("xmpp.domain");
        cacheurl = p.getProperty("cache.url");
        cacheport = p.getProperty("cache.port");
        appSecret = p.getProperty("user.info.appSecret");
        appKey = p.getProperty("user.info.appKey");
        weiXinCallback = p.getProperty("weiXin.callback");
        controlId = p.getProperty("control.id");
        nev = p.getProperty("nev");
        upload_prefix = p.getProperty("upload_prefix");
        privateBody = p.getProperty("bj.bdy.private");
        publicBody = p.getProperty("bj.bdy.public");
        appKeyUrl = p.getProperty("appKey.url");
        sasUrl = p.getProperty("sas.url");

    }
}