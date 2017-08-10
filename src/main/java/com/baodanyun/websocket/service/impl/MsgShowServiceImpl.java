package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.request.MaterialPageBean;
import com.baodanyun.websocket.bean.request.MsgShowBean;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.service.JedisService;
import com.baodanyun.websocket.service.externalInterface.MsgShowService;
import com.baodanyun.websocket.util.Config;
import com.baodanyun.websocket.util.HttpUtils;
import com.baodanyun.websocket.util.JSONUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liaowuhen on 2017/8/2.
 */
@Service
public class MsgShowServiceImpl implements MsgShowService {
    protected static Logger logger = LoggerFactory.getLogger(MsgShowServiceImpl.class);
    @Autowired
    private JedisService jedisService;

    @Override
    public Response update(String appKey, String action, MsgShowBean re) throws Exception {

        if (StringUtils.isEmpty(appKey) || StringUtils.isEmpty(action)) {
            throw new BusinessException("参数不能为空");
        }
        Map<String, String> mm = new HashMap<>();
        mm.put("appkey", appKey);
        mm.put("action", action);
        mm.put("content", JSONUtil.toJson(re));

        logger.info(JSONUtil.toJson(mm));
        jedisService.addMap(JedisServiceImpl.ENCRYPTCOUNT, re.getUsername(), re.getCount() + "");
        String str = HttpUtils.post(Config.sasUrl, mm);
        logger.info(str);

        if (StringUtils.isNotEmpty(str)) {
            return JSONUtil.toObject(Response.class, str);
        } else {
            throw new BusinessException("返回结果为空");
        }

    }

    @Override
    public Response getMaterial(String appKey, MaterialPageBean re) throws Exception {
        if (StringUtils.isEmpty(appKey)) {
            throw new BusinessException("appKey为空");
        }

        String action = null;
        if (StringUtils.isEmpty(re.getType())) {
            throw new BusinessException("获取素材类别为空");
        } else {
            if ("text".equals(re.getType())) {
                action = "gettextlist";
            } else if ("img".equals(re.getType())) {
                action = "getallpicturelist";
            } else if ("video".equals(re.getType())) {
                action = "getvideolist";
            } else if ("audio".equals(re.getType())) {
                action = "getvoicelist";
            } else {
                throw new BusinessException("不支持的类别" + re.getType());
            }
        }

        Map<String, String> mm = new HashMap<>();
        mm.put("appkey", appKey);
        mm.put("action", action);
        mm.put("content", JSONUtil.toJson(re));
        logger.info(JSONUtil.toJson(mm));
        String str = HttpUtils.post(Config.sasUrl, mm);

        if (StringUtils.isNotEmpty(str)) {
            return JSONUtil.toObject(Response.class, str);
        } else {
            throw new BusinessException("返回结果为空");
        }
    }
}
