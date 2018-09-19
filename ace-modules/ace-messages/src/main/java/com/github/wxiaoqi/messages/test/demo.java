package com.github.wxiaoqi.messages.test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * @Auther: JJY
 * @Date: 2018/9/19 15:21
 * @Description:
 */
public class demo {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1",6379);
        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                super.onMessage(channel, message);
                System.out.println("******************"+message);
            }
        }, "user:" + 999999 + ":todo:channel", "user:" + 999999 + ":message:channel");
    }

}
