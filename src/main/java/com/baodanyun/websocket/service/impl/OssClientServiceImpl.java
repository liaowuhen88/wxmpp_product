package com.baodanyun.websocket.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.baodanyun.websocket.service.OssClientService;
import com.baodanyun.websocket.util.Config;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liaowuhen on 2017/5/18.
 */
@Service
public class OssClientServiceImpl implements OssClientService {

    private static final String SPLIT_CHAR2 = "/";
    static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
    private static String hotfix = "/upload/down";//修复前缀
    private Logger logger = Logger.getLogger(OssClientServiceImpl.class);
    private String privateBucket = Config.privateBody;
    private String publicBucket = Config.publicBody;
    private String env = Config.nev;
    @Autowired
    private OSSClient ossClient;

    /**
     * 上传私有文件
     *
     * @param key
     * @param fin
     * @param contentType
     * @param async
     * @return
     * @throws OSSException
     * @throws ClientException
     * @throws IOException
     */
    @Override
    public String uploadPrivateFile(String env, String key, final InputStream fin, String contentType, boolean async) throws OSSException, ClientException, IOException {
        return uploadFile(env, key, fin, contentType, privateBucket, async);
    }

    /**
     * 上传公有文件
     *
     * @param key
     * @param fin
     * @param contentType
     * @param async
     * @return
     * @throws OSSException
     * @throws ClientException
     * @throws IOException
     */

    @Override
    public String uploadPublicFile(final String env, final String key, final InputStream fin, String contentType, boolean async) throws OSSException, ClientException, IOException {
        return uploadFile(env, key, fin, contentType, publicBucket, async);
    }

    /**
     * @param key
     * @param fin
     * @param contentType
     * @param bucketName
     * @param async       是否异步
     * @return
     * @throws OSSException
     * @throws ClientException
     * @throws IOException
     */
    @Override
    public String uploadFile(final String env, final String key, final InputStream fin, String contentType, final String bucketName, boolean async) throws OSSException, ClientException, IOException {
        final ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentLength(fin.available());

        if (StringUtils.isNotBlank(key)) {
            String fileName = key.toLowerCase();
            if (fileName.endsWith("png")) {
                contentType = "image/png";
            } else if (fileName.endsWith("jpg")) {
                contentType = "image/jpeg";
            } else if (fileName.endsWith("pdf")) {
                contentType = "application/pdf";
            }
        }

        if (StringUtils.isBlank(contentType)) {
            contentType = "application/octet-stream";
        }
        objectMeta.setContentType(contentType);

        final String path = getKey(env, key);

        if (async) {
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    ossClient.putObject(bucketName, path, fin, objectMeta);
                }
            });
        } else {
            ossClient.putObject(bucketName, path, fin, objectMeta);
        }
        return path;
    }

    /**
     * 如果不带环境就加上环境前缀
     * <p/>
     * dev 为开发环境
     *
     * @param key
     * @return
     */
    @Override
    public String getKey(String env, String key) {
        StringBuffer path = new StringBuffer();
        if (key.startsWith(env)) {
            path.append(key);
        } else if (key.startsWith(hotfix)) {
            int s = hotfix.length();
            path.append(env).append(key.substring(s, key.length()));
        } else if (key.startsWith(SPLIT_CHAR2)) {
            path.append(env).append(key);
        } else {
            path.append(env).append(SPLIT_CHAR2).append(key);
        }
        return path.toString();
    }

    @Override
    public InputStream downloadDJFile(String key) throws OSSException, ClientException {
        return downloadDJFile(env, key);
    }

    @Override
    public InputStream getPrivateInputStream(String key) {
        return null;
    }

    @Override
    public String genUrl(String key, Date expiration) {
        return null;
    }

    /**
     * 用getPrivateInputStream(String key)代替
     *
     * @param key
     * @return
     * @throws OSSException
     * @throws ClientException
     */
    @Deprecated
    @Override
    public InputStream downloadDJFile(String env, String key) throws OSSException, ClientException {
        OSSObject ossObject = ossClient.getObject(new GetObjectRequest(privateBucket, getKey(env, key)));
        long start = System.currentTimeMillis();
        InputStream inputStream = ossObject.getObjectContent();
        logger.debug(">>>>downloadFile:" + (System.currentTimeMillis() - start));
        return inputStream;
    }

    /**
     * @param key
     * @return
     */
    @Override
    public InputStream getPrivateInputStream(String env, String key) {
        InputStream result = null;

        OSSObject ossObject = ossClient.getObject(new GetObjectRequest(privateBucket, getKey(env, key)));
        InputStream inputStream = ossObject.getObjectContent();
        if (inputStream != null) {
            try {
                byte[] bytes = IOUtils.readStreamAsByteArray(inputStream);
                if (bytes != null && bytes.length > 0) {
                    result = new ByteArrayInputStream(bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.safeClose(inputStream);
            }
        }

        return result;
    }

    /*public InputStream getInputStream(String key) throws OSSException, ClientException {
        OSSObject ossObject = ossClient.getObject(new GetObjectRequest(privateBucket,  getKey(key)));
        return ossObject.getObjectContent();
    }*/


    /**
     * 生成签名授权地址
     *
     * @param key
     * @param expiration
     * @return
     */
    @Override
    public String genUrl(String env, String key, Date expiration) {
        URL url = ossClient.generatePresignedUrl(privateBucket, getKey(env, key), expiration);
        return url.toString();
    }


}
