package com.baodanyun.wxmpp.test.redisMQHandler;

import com.baodanyun.websocket.util.JSONUtil;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;

/**
 * Created by liaowuhen on 2017/7/20.
 */
public class RedisMQHandler extends JedisPubSub {

    @Override
    // 接收到消息后进行分发执行
    public void onMessage(String channel, String message) {
        System.out.println(channel + "," + message);
        Map map = null;
        try {
            map = JSONUtil.toObject(Map.class, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ("push".equals(channel)) {
            System.out.println("接收到一条推流消息，准备推流：" + map);
//          String appName=pushManager.push(map);
            //推流完成后还需要发布一个成功消息到返回队列
        } else if ("close".equals(channel)) {
            System.out.println("接收到一条关闭消息，准备关闭应用：" + map);
//          pushManager.closePush(appName);
        }
    }
}

