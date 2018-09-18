package com.github.wxiaoqi.messages.service.impl;

import com.github.wxiaoqi.messages.service.NewSubscriptionService;
import com.github.wxiaoqi.messages.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    String messages = new String();

    @Autowired
    private Jedis jedis;

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
        try {
            jedis.blpop(60, "user:" + uid + ":todo:channel", "user:" + uid + ":message:channel");
            //阻塞redis设置一分钟的过期时间,监控频道
            jedis.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    super.onMessage(channel, message);
                    messages = message;
                }
            }, "user:" + uid + ":todo:channel", "user:" + uid + ":message:channel");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            jedis.close();
        }
        jedis.close();
        return ResultUtil.returnSuccess(messages);
    }

}
