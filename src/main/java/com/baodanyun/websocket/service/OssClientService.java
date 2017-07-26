package com.baodanyun.websocket.service;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by liaowuhen on 2017/5/18.
 */
@Service
public interface OssClientService {

    String uploadPrivateFile(String env, String key, InputStream fin, String contentType, boolean async) throws OSSException, ClientException, IOException;

    /**
     * key 为oss相对目录
     *
     * @param env
     * @param key
     * @param fin
     * @param contentType
     * @param async
     * @return
     * @throws OSSException
     * @throws ClientException
     * @throws IOException
     */
    String uploadPublicFile(String env, String key, InputStream fin, String contentType, boolean async) throws OSSException, ClientException, IOException;

    String uploadFile(String env, String key, InputStream fin, String contentType, String bucketName, boolean async) throws OSSException, ClientException, IOException;

    /**
     * 如果不带环境就加上环境前缀
     *
     * @param key
     * @return
     */
    String getKey(String env, String key);

    /**
     * 用getPrivateInputStream(String key)代替
     *
     * @param key
     * @return
     * @throws OSSException
     * @throws ClientException
     */
    @Deprecated
    InputStream downloadDJFile(String key) throws OSSException, ClientException;

    /**
     * @param key
     * @return
     */
    InputStream getPrivateInputStream(String key);


    /**
     * 生成签名授权地址
     *
     * @param key
     * @param expiration
     * @return
     */
    String genUrl(String key, Date expiration);

    @Deprecated
    InputStream downloadDJFile(String env, String key) throws OSSException, ClientException;

    InputStream getPrivateInputStream(String env, String key);

    String genUrl(String env, String key, Date expiration);
}
