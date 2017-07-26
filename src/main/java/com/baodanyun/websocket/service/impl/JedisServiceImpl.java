package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.service.JedisService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.List;

/**
 * Created by liaowuhen on 2017/7/20.
 */

@Service
public class JedisServiceImpl implements JedisService {
    public static final String displayStatus = "displayStatus";
    private static Logger logger = LoggerFactory.getLogger(JedisServiceImpl.class);
    @Autowired
    private JedisPool jedisPool;

    @Override
    public void addMap(String redisKey, String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hset(redisKey, key, value);

        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }

    }

    @Override
    public String getFromMap(String redisKey, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<String> list = jedis.hmget(redisKey, key);
            if (null != list && list.size() == 1 && StringUtils.isNotEmpty(list.get(0))) {
                return list.get(0);
            }
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }

        return null;
    }

    /**
     * 推入消息到redis消息通道
     */

    @Override
    public void publish(String channel, String message) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.publish(channel, message);
        } finally {
            jedis.close();
        }
    }

    @Override
    public void subscribe(String channel, JedisPubSub jedisPubSub) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.subscribe(jedisPubSub, channel);
        } finally {
            jedis.close();
        }
    }

    @Override
    public void product(String channel, String message) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.lpush(channel, message);
        } finally {
            jedis.close();
        }
    }

    @Override
    public void consume(String channel, int timeout) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            while (true) {
                List<String> list = jedis.brpop(timeout, channel);
                dealConsumeMsg(list);
            }
        } finally {
            jedis.close();
        }
    }

    @Override
    public void dealConsumeMsg(List<String> list) {
        try {
            logger.info(list.toString());
        } catch (Exception e) {
            logger.error("error", e);
        }
    }
}
