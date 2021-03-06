package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.pageSearch.OfMessagearchiveSearchPage;
import com.baodanyun.websocket.model.OfMessagearchiveWithBLOBs;
import com.baodanyun.websocket.service.OfMessagearchiveService;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.Render;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liaowuhen on 2017/7/1.
 */
@RestController
public class OfMessagearchiveConTroller extends BaseController {
    protected static Logger logger = LoggerFactory.getLogger(OfMessagearchiveConTroller.class);
    @Autowired
    private OfMessagearchiveService ofMessagearchiveService;

    @RequestMapping(value = "getOfMessageArchiveByJid")
    public void getByJid(OfMessagearchiveSearchPage page, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        //客服必须填写用户名 和 密码
        logger.info("user" + JSONUtil.toJson(page));
        Response response = new Response();
        page.setJid("xvql1127@conference.126xmpp");
        PageInfo<OfMessagearchiveWithBLOBs> li = ofMessagearchiveService.select(page);
        response.setData(li);
        response.setSuccess(true);

        Render.r(httpServletResponse, JSONUtil.toJson(response));

    }

    @RequestMapping(value = "getGroupMessageCount")
    public void getByJid(String date, HttpServletResponse httpServletResponse) {
        //客服必须填写用户名 和 密码
        Response response = new Response();
        Long count = ofMessagearchiveService.getGroupMessageCount(date);
        response.setData(count);
        response.setSuccess(true);

        Render.r(httpServletResponse, JSONUtil.toJson(response));

    }


}
