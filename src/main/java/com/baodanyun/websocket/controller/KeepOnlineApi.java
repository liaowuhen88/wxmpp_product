package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.service.JedisService;
import com.baodanyun.websocket.util.BaseUtil;
import com.baodanyun.websocket.util.CommonConfig;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yutao on 2016/10/10.
 */
@RestController
public class KeepOnlineApi extends BaseController {

    protected static Logger logger = LoggerFactory.getLogger(KeepOnlineApi.class);

    private static Map<String, Object> plugin;

    @Autowired
    private JedisService jedisService;

    public static void main(String[] args) {
        Object m1 = 1.0;
        Object m2 = 1.0;

        //String l = (String)m1;
        System.out.print(m1.equals(m2));
    }

    /**
     * 保持在线的接口
     *
     * @param httpServletResponse
     */
    @RequestMapping(value = "keepOnline", method = RequestMethod.POST)
    public void keepOnline(HttpServletRequest request, HttpServletResponse httpServletResponse) {

        AbstractUser au = (AbstractUser) request.getSession().getAttribute(Common.USER_KEY);

        if (null != au) {
            logger.info("au.getLoginUsername:[" + au.getLoginUsername() + "]----------au.getUserName():[" + au.getUserName() + "]----keepOnline");

        } else {
            logger.info("au.getLoginUsername:[is null]");

        }

        Response response = new Response();

        response.setSuccess(true);

        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }

    @RequestMapping(value = "pluginOnline", method = RequestMethod.POST)
    public void pluginOnline(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        AbstractUser au = (AbstractUser) request.getSession().getAttribute(Common.USER_KEY);
        Response response = new Response();
        try {
            if (null != au) {
                String jids = jedisService.getFromMap(CommonConfig.ZX_CJ_INFO, au.getId());
                Map maps = JSONUtil.toObject(Map.class, jids);

                if (null != plugin) {
                    Iterator<Map.Entry<String, Object>> iter1 = maps.entrySet().iterator();
                    while (iter1.hasNext()) {
                        Map.Entry<String, Object> entry1 = iter1.next();
                        Object m1value = entry1.getValue() == null ? "" : entry1.getValue();
                        Object m2value = plugin.get(entry1.getKey()) == null ? "" : plugin.get(entry1.getKey());
                        //
                        if (!m1value.equals(m2value)) {//若两个map中相同key对应的value不相等
                            Integer redi = BaseUtil.ObjectToInt(m2value);
                            Map map = new HashMap();
                            map.put(entry1.getKey(), redi);
                            response.setData(map);
                        }
                    }
                }

                plugin = maps;
            }

        } catch (Exception e) {
            logger.error("error", e);
        }

        response.setSuccess(true);
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }
}
