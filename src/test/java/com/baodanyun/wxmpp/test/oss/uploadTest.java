package com.baodanyun.wxmpp.test.oss;


import com.baodanyun.websocket.service.UploadService;
import com.baodanyun.wxmpp.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by liaowuhen on 2017/5/18.
 */
public class uploadTest extends BaseTest {

    @Autowired
    private UploadService uploadService;

    @Test
    public void upload() throws IOException {
        File file = new File("C:\\Users\\liaowuhen\\Desktop\\init.bat");

        FileInputStream fileInputStream = new FileInputStream(file);
        String key = "liaowuhen/" + file.getName();

        uploadService.upload(key, fileInputStream);

    }
}
