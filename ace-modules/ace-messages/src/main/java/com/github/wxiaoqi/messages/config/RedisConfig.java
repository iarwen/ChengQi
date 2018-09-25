package com.github.wxiaoqi.messages.config;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;

import java.util.Objects;

@Configuration
@ConfigurationProperties("redis")
@AutoConfigureAfter
public class RedisConfig{

    private  Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    //Redis 地址
    private static String host;

    //Redis 端口号
    private static  String port;

    //Redis 密码
    private static  String password;

    //Redis 默认地址
    private static String defHost;

    //Redis 默认端口号
    private static  String  defPort;

    public void setHost(String host) {
        defHost = host;
        this.host = host;
    }

    public  String getHost() {
        return host;
    }


    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        defPort = port;
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public Jedis jedisTemplate(Class tClass){
        Jedis jedis = null;
        String host = new RedisConfig().getHost();
        String port = new RedisConfig().getPort();
        String password = new RedisConfig().getPassword();
        logger.info("host ======> "+host);
        logger.info("port ======> "+port);
        try{
            logger.info(tClass.getName()+"---->开始连接Redis");
            if(host!=null && port!=null){
                defHost = host;
                defPort = port;
                jedis = new Jedis(defHost,Integer.parseInt(defPort));
                if(!Objects.isNull(password) && !ObjectUtils.isEmpty(password)){
                    jedis.auth(password);
                }
                logger.info("当前Redis连接的地址为： "+defHost+":"+defPort);
            }else {
                logger.info("Redis链接超时尝试链接本地Redis");
                defHost = "127.0.0.1";
                defPort = "6379";
                jedis = new Jedis(defHost,Integer.parseInt(defPort));
                logger.info("当前Redis连接的地址为： "+defHost+":"+defPort);
            }
            logger.info(tClass.getName()+"---->Redis连接完成");

        }catch (Exception e){
            e.printStackTrace();
            logger.info("Redis 连接异常: "+ JSON.toJSONString(e.getMessage()));
        }
        return jedis;
    }

}
