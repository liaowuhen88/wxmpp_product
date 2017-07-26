package com.baodanyun.websocket.springConfig;

import com.alibaba.druid.pool.DruidDataSource;
import com.aliyun.oss.OSSClient;
import com.baodanyun.websocket.util.Config;
import com.baodanyun.websocket.util.PropertiesUtil;
import com.github.pagehelper.PageHelper;
import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.jms.Destination;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

/**
 * Created by liaowuhen on 2016/11/2.
 */
@Component
@EnableScheduling
@ComponentScan(basePackages =
        {
                "com.oss",
                "com.baodanyun.websocket.service",
                "com.baodanyun.websocket.controller",
                "com.baodanyun.websocket.listener"
        })
//加载资源文件
@PropertySource({"classpath:spring-config.properties",
        "classpath:spring-oss.properties"
})
public class SpringConfig {
    protected static Logger logger = LoggerFactory.getLogger(SpringConfig.class);
    Map<String, String> map = PropertiesUtil.get(this.getClass().getClassLoader(), "spring-config.properties");
    Map<String, String> ossMap = PropertiesUtil.get(this.getClass().getClassLoader(), "spring-oss.properties");

    private String endpoint = ossMap.get("oss.endpoint");
    private String accessKeyId = ossMap.get("oss.accessKeyId");
    private String secretAccessKey = ossMap.get("oss.secretAccessKey");

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DataSource dataSource() {
        logger.info("DataSource");
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(map.get("jdbc_driverClassName"));
        dataSource.setUrl(map.get("jdbc_url"));
        dataSource.setUsername(map.get("jdbc_username"));
        dataSource.setPassword(map.get("jdbc_password"));
        dataSource.setMaxActive(50);
        dataSource.setInitialSize(5);
        dataSource.setMaxWait(6000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeoutMillis(1800);
        dataSource.setLogAbandoned(true);
        //dataSource.setFilters("mergeStat");
        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager txManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        logger.info("sqlSessionFactory");
        TransactionFactory transactionFactory = new
                JdbcTransactionFactory();

        Environment environment =
                new Environment("development", transactionFactory, dataSource);

        PageHelper ph = new PageHelper();
        Properties p = new Properties();
        p.put("dialect", "mysql");
        p.put("offsetAsPageNum", "true");
        p.put("rowBoundsWithCount", "true");
        p.put("pageSizeZero", "true");
        p.put("reasonable", "false");
        p.put("params", "pageNum=start;pageSize=limit;");
        p.put("returnPageInfo", "check");

        ph.setProperties(p);
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration(environment);

        configuration.addInterceptor(ph);

        SqlSessionFactory sessionFactory =
                new SqlSessionFactoryBuilder().build(configuration);

        return sessionFactory;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactory(sqlSessionFactory);
        mapperScannerConfigurer.setBasePackage("com/baodanyun/websocket/dao");
        return mapperScannerConfigurer;
    }


    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }


    /**
     * MemCached
     */

    @Bean
    public SockIOPool getSockIOPool() {
        String[] servers = {Config.cacheurl + ":" + Config.cacheport};
        SockIOPool pool = SockIOPool.getInstance();
        pool.setServers(servers);
        pool.setFailover(false);
        pool.setInitConn(10);
        pool.setMinConn(5);
        pool.setMaxConn(250);
        pool.setMaintSleep(30);
        pool.setNagle(false);
        pool.setSocketTO(3000);
        pool.setAliveCheck(true);
        pool.initialize();
        return pool;
    }

    @Bean
    public MemCachedClient getMemCachedClient() {
        MemCachedClient memCachedClient = new MemCachedClient();
        return memCachedClient;
    }


    /**
     * active
     */

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory aMQF = new ActiveMQConnectionFactory();
        aMQF.setBrokerURL(map.get("broker.url"));
        return aMQF;
    }


    @Bean
    public JmsTemplate jmsTemplate(@Qualifier("pooledConnectionFactory") PooledConnectionFactory pooledConnectionFactory) {

        JmsTemplate jtl = new JmsTemplate();
        jtl.setConnectionFactory(pooledConnectionFactory);
        return jtl;
    }


    @Bean
    public PooledConnectionFactory pooledConnectionFactory(@Qualifier("activeMQConnectionFactory") ActiveMQConnectionFactory activeMQConnectionFactory) {
        PooledConnectionFactory pCF = new PooledConnectionFactory();
        pCF.setConnectionFactory(activeMQConnectionFactory);
        return pCF;
    }

    @Bean
    public Destination destination() {
        Destination de = new ActiveMQQueue(map.get("env.queue.eventCenterDestination"));

        return de;
    }

    /*
     redis 接入
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
       /* config.setMaxTotal(60000);//设置最大连接数
        config.setMaxIdle(1000); //设置最大空闲数
        config.setMaxWaitMillis(3000);//设置超时时间
        config.setTestOnBorrow(true);*/
        return jedisPoolConfig;
    }

    @Bean
    public JedisPool jedisPool(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig) {
        String host = map.get("redis.hostName");
        int port = Integer.valueOf(map.get("redis.port"));
        int timeout = Integer.valueOf(map.get("redis.timeout"));
        String password = map.get("redis.password");

        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);

        return jedisPool;
    }


    /**
     * 文件上传
     *
     * @return
     */
    @Bean
    public MultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }


    /**
     * oss
     *
     * @return
     */
    @Bean
    public OSSClient ossClient() {
        return new OSSClient(endpoint, accessKeyId, secretAccessKey);
    }

}
