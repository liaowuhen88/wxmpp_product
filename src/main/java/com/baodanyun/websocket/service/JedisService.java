package com.baodanyun.websocket.service;

import redis.clients.jedis.JedisPubSub;

import java.util.List;
import java.util.Map;

/**
 * Created by liaowuhen on 2017/7/20.
 */

public interface JedisService {

    void addValue(String key, String value);

    String getValue(String key);

    void removeKey(String key);

    void addMap(String redisKey, String key, String value);

    String getFromMap(String redisKey, String key);

    Boolean isExitMap(String redisKey, String key);

    Map<String, String> getAllFromMap(String redisKey);

    void removeFromMap(String redisKey, String key);

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
