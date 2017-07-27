package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.response.FIleResponse;
import com.baodanyun.websocket.service.UploadService;
import com.baodanyun.websocket.util.AccessControlAllowUtils;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.Render;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * Created by yutao on 2016/10/4.
 */
@RestController
public class FileUploadApi extends BaseController {

    protected static Logger logger = LoggerFactory.getLogger(CustomerApi.class);

    @Autowired
    private UploadService uploadService;

    @RequestMapping(value = "fileUpload/{user}")
    public void customerLogin(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        //客服必须填写用户名 和 密码
        Response response = new Response();
        String fileName = request.getParameter("name");
        try {
            String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

            InputStream is = request.getInputStream();
            int size = request.getContentLength();
            byte[] reqBodyBytes = uploadService.readBytes(is, size);
            InputStream stream = new ByteArrayInputStream(reqBodyBytes);
            String key = uploadService.upload(fileName, stream);

            key = url + "/api/downLoad?key=" + key;
            FIleResponse fr = new FIleResponse();
            fr.setSrc(key);

            response.setData(fr);
            response.setSuccess(true);
        } catch (Exception e) {
            response.setSuccess(false);
            logger.error("", e);
        }

        AccessControlAllowUtils.access(httpServletResponse);
        Render.r(httpServletResponse, JSONUtil.toJson(response));
    }


    @RequestMapping(value = "downLoad", method = {RequestMethod.POST, RequestMethod.GET})
    public void uploadDj(String type, String key, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccessControlAllowUtils.access(response);
        request.setCharacterEncoding("UTF-8");
        String filename = key.substring(key.lastIndexOf("\\") + 1);

        if (StringUtils.isNotEmpty(type)) {
            if (type.equals("image")) {
                response.setContentType("image/jpeg");
            } else if (type.equals("voice")) {
                response.setContentType("application/ogg");
                //response.setHeader("content-disposition", "attachment;filename="+key.hashCode()+".mp3");
            }
        }
        response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));

        ServletOutputStream stream = response.getOutputStream();
        //FileInputStream in = new FileInputStream(file);
        InputStream in = uploadService.downloadDJFile(key);

        if (in != null) {
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = in.read(b)) != -1) {
                //写入输出流
                stream.write(b, 0, len);
            }
        }

        stream.flush();
        stream.close();

    }

}
