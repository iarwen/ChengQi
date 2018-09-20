package com.github.wxiaoqi.messages.redisconfig;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {

    private Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private String redisPort;

    @Value("${redis.password}")
    private String redisPwd;

    private String defaultHost;
    private String defaultPort;

    @Bean
    public Jedis jedisTemplate(){
        Jedis jedis = null;
        try{
            logger.info("开始连接Redis");
            if(redisHost!=null && redisPort!=null){
                defaultHost = redisHost;
                defaultPort = redisPort;
                jedis = new Jedis(defaultHost,Integer.parseInt(defaultPort));
              //  jedis.auth(redisPwd);
            }else {
                logger.info("Redis链接超时尝试链接本地Redis");
                defaultHost = "127.0.0.1";
                defaultPort = "6379";
                jedis = new Jedis(defaultHost,Integer.parseInt(defaultPort));
            }
            logger.info("Redis连接完成");
            logger.info("当前Redis连接的地址为： "+redisHost+":"+redisPort);
        }catch (Exception e){
            e.printStackTrace();
            logger.info("Redis 连接异常: "+ JSON.toJSONString(e.getMessage()));
        }
        return jedis;
    }
}
