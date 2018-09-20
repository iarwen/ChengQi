package com.github.wxiaoqi.messages.service.impl;

import com.github.wxiaoqi.messages.config.RedisConfig;
import com.github.wxiaoqi.messages.service.NewSubscriptionService;
import com.github.wxiaoqi.messages.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;


/**
 * @Auther: JJY
 * @Date: 2018/9/18 10:51
 * @Description:
 */
@Service
@Slf4j
public class NewSubscriptionServiceImpl implements NewSubscriptionService {

     private String messages = "";

     private  Jedis jedis =  new RedisConfig().jedisTemplate(NewSubscriptionServiceImpl.class);

    /**
     * 功能描述: 消息订阅
     *
     * @param: uid
     * @return: Result
     * @auther: JJY
     * @date: 2018/9/18
     */
    @Override
    public ResultUtil newSubscription(Long uid) {
        if (ObjectUtils.isEmpty(uid)) {
            log.error("传入uid为空");
            ResultUtil.returnError("传入uid为空", 500);
        }
        //启动新的线程
        new Thread(() -> {
              Jedis jedis =  new RedisConfig().jedisTemplate(Thread.class);
            try {
                String message = "";
                Thread.sleep(60000);
                log.info("*************@@@@@@@@@@@@");
                jedis.publish("user:"+999999+":message:channel", message);
               log.info("==============添加uid=999999的模拟信息成功=========="+message);
                jedis.close();
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        try {
            jedis.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    super.onMessage(channel, message);
                    System.out.println("******************"+message);
                    messages = message;
                    unsubscribe();
                }
            }, "user:" + uid + ":todo:channel", "user:" + uid + ":message:channel","user:"+999999+":message:channel");

        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.returnError("=============="+e);
        }
        jedis.close();
        return ResultUtil.returnSuccess(messages);
    }
}
