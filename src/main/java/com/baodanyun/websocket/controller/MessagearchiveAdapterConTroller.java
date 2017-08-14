package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.pageSearch.MessagearchiveAdapterSearchPage;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.model.MessageArchiveAdapter;
import com.baodanyun.websocket.service.MessageArchiveAdapterService;
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
public class MessagearchiveAdapterConTroller extends BaseController {
    protected static Logger logger = LoggerFactory.getLogger(MessagearchiveAdapterConTroller.class);
    @Autowired
    private MessageArchiveAdapterService messageArchiveAdapterService;

    @RequestMapping(value = "selectByFromAndTo")
    public void selectByFromAndTo(MessagearchiveAdapterSearchPage page, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        //客服必须填写用户名 和 密码
        logger.info("user" + JSONUtil.toJson(page));
        Response response = new Response();


        PageInfo<MessageArchiveAdapter> li = null;
        try {
            li = messageArchiveAdapterService.selectByFromAndTo(page);
            response.setData(li);
            response.setSuccess(true);
        } catch (BusinessException e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setMsg(e.getMessage());
        }

        Render.r(httpServletResponse, JSONUtil.toJson(response));

    }

}
