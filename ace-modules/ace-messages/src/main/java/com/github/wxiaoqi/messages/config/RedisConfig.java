package com.github.wxiaoqi.messages.config;


import com.alibaba.fastjson.JSON;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;

import java.util.Objects;

@Configuration
//@EnableApolloConfig
public class RedisConfig {

    private Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    @Value("${redis.host}")
   // @ApolloConfig("redis.host")
    private String redisHost;

    @Value("${redis.port}")
   // @ApolloConfig("redis.port")
    private String redisPort;

    @Value("${redis.password}")
    //@ApolloConfig("redis.password")
    private String redisPwd;


    private String defaultHost;
    private String defaultPort;

    public Jedis jedisTemplate(Class tClass){
        Jedis jedis = null;
        try{
            logger.info(tClass.getName()+"---->开始连接Redis");
            if(redisHost!=null && redisPort!=null){
                defaultHost = redisHost;
                defaultPort = redisPort;
                jedis = new Jedis(defaultHost,Integer.parseInt(defaultPort));
                if(!Objects.isNull(redisPwd) && !ObjectUtils.isEmpty(redisPwd)){
                    jedis.auth(redisPwd);
                }
                logger.info("当前Redis连接的地址为： "+defaultHost+":"+defaultPort);
            }else {
                logger.info("Redis链接超时尝试链接本地Redis");
                defaultHost = "127.0.0.1";
                defaultPort = "6379";
                jedis = new Jedis(defaultHost,Integer.parseInt(defaultPort));
                logger.info("当前Redis连接的地址为： "+defaultHost+":"+defaultPort);
            }
            logger.info(tClass.getName()+"---->Redis连接完成");

        }catch (Exception e){
            e.printStackTrace();
            logger.info("Redis 连接异常: "+ JSON.toJSONString(e.getMessage()));
        }
        return jedis;
    }
}
