package com.baodanyun.websocket.service;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liaowuhen on 2017/5/18.
 */
public interface UploadService {
    void upload(String fileName) throws IOException;


    void upload(File file) throws IOException;

    String upload(String fileName, InputStream inputStream) throws IOException;

    byte[] readBytes(InputStream is, int contentLen);


    InputStream downloadDJFile(String key) throws OSSException, ClientException;
}
