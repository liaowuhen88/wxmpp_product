package com.baodanyun.websocket.service.impl;


import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.baodanyun.websocket.service.OssClientService;
import com.baodanyun.websocket.service.UploadService;
import com.baodanyun.websocket.util.Config;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by liaowuhen on 2017/5/18.
 */

@Service
public class UploadServiceImpl implements UploadService {

    protected static final Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class);

    @Autowired
    private OssClientService ossClientService;
    private String uploadPrefix = Config.upload_prefix;
    private String nev = Config.nev;

    @Override
    public void upload(String fileName) throws IOException {
        File file = new File(fileName);
        upload(file);
    }

    @Override
    public void upload(File file) throws IOException {
        if (null != file) {
            if (file.exists()) {
                if (file.isFile()) {

                    logger.info("ready to upload[" + file.getAbsolutePath() + "]");
                    try {
                        if (!file.getName().contains("bat")) {
                            // File file = new File("C:\\Users\\liaowuhen\\Desktop\\2017-05-12.zip");
                            FileInputStream fileInputStream = new FileInputStream(file);

                            String key = uploadPrefix + "/" + DateFormatUtils.format(System.currentTimeMillis(), DateFormatUtils.ISO_DATE_FORMAT.getPattern()) + "/" + file.getName();

                            String response = ossClientService.uploadPrivateFile(nev, key, fileInputStream, "application/json", false);

                            logger.info(response);

                            boolean flag = file.renameTo(new File(file.getAbsoluteFile() + "bat"));

                            logger.info("[" + file.getName() + "]文件名称修改" + flag);

                        } else {
                            logger.info("[" + file.getName() + "]已上传，忽略");
                        }

                    } catch (Exception e) {
                        logger.error("error", e);
                    }


                } else if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (null != files) {
                        for (int i = 0; i < files.length; i++) {
                            File cfile = files[i];
                            upload(cfile);
                        }
                    }
                }
            } else {
                logger.error("文件不存在");
            }


        } else {
            logger.error("[" + file.getAbsolutePath() + "]文件为空");
        }
    }

    @Override
    public String upload(String fileName, InputStream inputStream) throws IOException {
        String key = uploadPrefix + "/" + DateFormatUtils.format(System.currentTimeMillis(), DateFormatUtils.ISO_DATE_FORMAT.getPattern()) + "/" + fileName;

        //ossClientService.u
        String response = ossClientService.uploadPrivateFile(nev, key, inputStream, "application/octet-stream", false);
        logger.info(response);
        return key;
    }

    @Override
    public byte[] readBytes(InputStream is, int contentLen) {
        if (contentLen > 0) {
            int readLen = 0;
            int readLengthThisTime = 0;
            byte[] message = new byte[contentLen];
            try {
                while (readLen != contentLen) {
                    readLengthThisTime = is.read(message, readLen, contentLen - readLen);
                    if (readLengthThisTime == -1) {// Should not happen.
                        break;
                    }
                    readLen += readLengthThisTime;
                }
                return message;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[]{};
    }

    @Override
    public InputStream downloadDJFile(String key) throws OSSException, ClientException {
        return ossClientService.downloadDJFile(key);
    }


}
