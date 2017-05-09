package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.util.HttpUtils;
import com.baodanyun.websocket.util.Render;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yutao on 2016/10/4.
 */
@RestController
public class ImageApi extends BaseController{
    protected static Logger logger = Logger.getLogger(CustomerApi.class);

    @RequestMapping(value = "findLoginImage")
    public void api(HttpServletRequest request,HttpServletResponse response){
        String json="";
        try {
            json= HttpUtils.get("http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN");
        } catch (IOException e) {
            logger.error(e);
        }
        Render.r(response, json);
    }
}
