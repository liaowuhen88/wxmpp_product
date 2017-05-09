package com.baodanyun.websocket.springConfig;

import com.alibaba.druid.pool.DruidDataSource;
import com.baodanyun.websocket.util.Config;
import com.baodanyun.websocket.util.PropertiesUtil;
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
import org.apache.log4j.Logger;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by liaowuhen on 2016/11/2.
 */
@Component
@EnableScheduling
@ComponentScan(basePackages =
        {"com.baodanyun.websocket.service",
                "com.baodanyun.websocket.controller",
                "com.baodanyun.websocket.listener"
        })
//加载资源文件
@PropertySource({"classpath:config.properties"})
public class SpringConfig {
    protected static Logger logger = Logger.getLogger(SpringConfig.class);
    Map<String, String> map = PropertiesUtil.get(this.getClass().getClassLoader(), "config.properties");


    @Bean
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
    public DataSourceTransactionManager txManager() {

        return new DataSourceTransactionManager(dataSource());
    }

    //@Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        //DataSource dataSource = SpringContextUtil.getBean("dataSource", DataSource.class);

        TransactionFactory transactionFactory = new
                JdbcTransactionFactory();

        Environment environment =
                new Environment("development", transactionFactory, dataSource());

        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration(environment);

        SqlSessionFactory sessionFactory =
                new SqlSessionFactoryBuilder().build(configuration);
        return sessionFactory;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() throws Exception {
      //  SqlSessionFactory sqlSessionFactory = SpringContextUtil.getBean("sqlSessionFactory", SqlSessionFactory.class);

        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactory(sqlSessionFactory());
        mapperScannerConfigurer.setBasePackage("com/baodanyun/websocket/dao");
        return mapperScannerConfigurer;
    }


    @Bean
    public JdbcTemplate jdbcTemplate() {
        //DataSource dataSource = SpringContextUtil.getBean("dataSource", DataSource.class);

        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource());
        return jdbcTemplate;
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory aMQF = new ActiveMQConnectionFactory();
        aMQF.setBrokerURL(map.get("broker.url"));
        return aMQF;
    }

    @Bean
    public PooledConnectionFactory pooledConnectionFactory() {
        //ActiveMQConnectionFactory activeMQConnectionFactory = SpringContextUtil.getBean("activeMQConnectionFactory", ActiveMQConnectionFactory.class);

        PooledConnectionFactory pCF = new PooledConnectionFactory();
        pCF.setConnectionFactory(activeMQConnectionFactory());
        return pCF;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        //PooledConnectionFactory pooledConnectionFactory = SpringContextUtil.getBean("pooledConnectionFactory", PooledConnectionFactory.class);

        JmsTemplate jtl = new JmsTemplate();
        jtl.setConnectionFactory(pooledConnectionFactory());
        return jtl;
    }

    @Bean
    public Destination destination() {
        Destination de = new ActiveMQQueue(map.get("env.queue.eventCenterDestination"));

        return de;
    }

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
        MemCachedClient  memCachedClient= new  MemCachedClient();
        return memCachedClient;
    }
}
