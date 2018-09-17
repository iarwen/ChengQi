package com.github.wxiaoqi.messages.redisconfig;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {

    private Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private String redisPort;

    private String defaultHost="127.0.0.1";
    private String defaultPort="6379";

    @Bean
    public Jedis jedisTemplate(){
        logger.info("开始连接Redis");
        if(redisHost!=null && redisPort!=null){
            defaultHost = redisHost;
            defaultPort = redisPort;
        }
        Jedis jedis = new Jedis(defaultHost,Integer.parseInt(defaultPort));
        logger.info("Redis连接完成");
        logger.info("当前Redis连接的地址为： "+redisHost+":"+redisPort);
        return jedis;
    }
}
