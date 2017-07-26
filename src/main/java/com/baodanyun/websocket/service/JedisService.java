package com.baodanyun.websocket.service;

import redis.clients.jedis.JedisPubSub;

import java.util.List;

/**
 * Created by liaowuhen on 2017/7/20.
 */

public interface JedisService {
    void addMap(String redisKey, String key, String value);

    String getFromMap(String redisKey, String key);

    /**
     * 发布者
     *
     * @param channel
     * @param message
     */
    void publish(String channel, String message);

    void subscribe(String channel, JedisPubSub jedisPubSub);

    /**
     * 生产者
     *
     * @param channel
     * @param message
     */
    void product(String channel, String message);


    /**
     * 消费者
     *
     * @param channel
     */
    void consume(String channel, int timeout);


    /**
     * 处理消费者接收到的消息
     *
     * @param list
     */
    void dealConsumeMsg(List<String> list);
}
