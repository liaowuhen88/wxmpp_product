package com.baodanyun.wxmpp.test;


import com.baodanyun.websocket.service.JedisService;
import com.baodanyun.websocket.service.impl.MessageFiterServiceImpl;
import com.baodanyun.wxmpp.test.redisMQHandler.RedisMQHandler;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 峥桂 on 2016/11/15.
 */
public class RedisTest extends BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(RedisTest.class);
    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private JedisService jedisService;


    @Test
    public void desc() {
        try {
            Jedis jedis = jedisPool.getResource();
            jedis.flushDB();
            Map<String, String> map = new HashMap<>();
            map.put("key1", "value1");
            map.put("key2", "value2");
            map.put("key3", "value3");
            map.put("key4", "value4");
            jedis.hmset("hash", map);
            jedis.hset("hash", "key4", "value5");

            System.out.println("散列hash的所有键值对为：" + jedis.hgetAll("hash"));//return Map<String,String>
            System.out.println("散列hash的所有键为：" + jedis.hkeys("hash"));//return Set<String>
            System.out.println("散列hash的所有值为：" + jedis.hvals("hash"));//return List<String>
            System.out.println("将key6保存的值加上一个整数，如果key6不存在则添加key6：" + jedis.hincrBy("hash", "key6", 6));
            System.out.println("散列hash的所有键值对为：" + jedis.hgetAll("hash"));
            System.out.println("将key6保存的值加上一个整数，如果key6不存在则添加key6：" + jedis.hincrBy("hash", "key6", 3));
            System.out.println("散列hash的所有键值对为：" + jedis.hgetAll("hash"));
            System.out.println("删除一个或者多个键值对：" + jedis.hdel("hash", "key2"));
            System.out.println("散列hash的所有键值对为：" + jedis.hgetAll("hash"));
            System.out.println("散列hash中键值对的个数：" + jedis.hlen("hash"));
            System.out.println("判断hash中是否存在key2：" + jedis.hexists("hash", "key2"));
            System.out.println("判断hash中是否存在key3：" + jedis.hexists("hash", "key3"));
            System.out.println("获取hash中的值：" + jedis.hmget("hash", "key3"));
            System.out.println("获取hash中的值：" + jedis.hmget("hash", "key8"));
            System.out.println("获取hash中的值：" + jedis.hmget("hash", "key3", "key4"));
            jedis.close();

        } catch (Exception e) {
            logger.error("", e);
        }
    }


    @Test
    public void push() {
        jedisService.publish("channel_a", "{\"message\":\"message_a\"}");
    }

    @Test
    public void subscribe() {
        RedisMQHandler rm = new RedisMQHandler();
        jedisService.subscribe("channel_a", rm);
    }

    @Test
    public void product() {
        jedisService.product("channel_a", "{\"message\":\"message_a\"}");
    }

    @Test
    public void consume() {
        jedisService.consume("computationalCosts", 30);
    }

    @Test
    public void addMap() {
        jedisService.addMap("displayStatus", "yt_zwc@126xmpp", "1");
    }

    @Test
    public void getFromMap() {
        String id = jedisService.getFromMap("Conversation_zwc@126xmpp", "666@conference.126xmpp");
        System.out.println(id);
    }

    @Test
    public void addValue() {
        jedisService.addValue("zwc" + MessageFiterServiceImpl.SWITCH, "1");
        jedisService.addValue("zwc@126xmpp", "1");
        jedisService.addValue("yt_aaaa@126xmpp", "1");
    }

    @Test
    public void getValue() {
        System.out.println(jedisService.getValue("yt_zwc@126xmpp"));
    }
}
