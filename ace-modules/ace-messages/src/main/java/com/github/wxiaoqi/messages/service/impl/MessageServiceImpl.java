package com.github.wxiaoqi.messages.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.messages.entity.Messages;
import com.github.wxiaoqi.messages.service.MessageService;
import com.github.wxiaoqi.messages.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import redis.clients.jedis.Jedis;
import sun.plugin2.message.Message;

import java.util.HashMap;

/**
 *  梁健
 *  2018/9/17 15:49
 *  消息推送Service
 */
@Slf4j
@Service
//@Transactional(rollbackFor = Exception.class,readOnly = true)
public class MessageServiceImpl implements MessageService {

    Jedis jedis = new Jedis("127.0.0.1",6379);

    /**
     * 发布消息
     * 梁健
     * 2018/09/17 15:45
     * @param messages
     */
    @Override
    public ResultUtil releaseTheMessage(Messages messages) {
        try{
            log.info("传入的参数为："+JSON.toJSONString(messages));
            HashMap<String,Object> hashMap = new HashMap<>(16);
            String[] to = messages.getTo();
            //抽取 主送信息
            for (String str : to ) {
                String[] split = str.split(":");
                hashMap.put(split[0],split[1]);
            }
            log.info(JSON.toJSONString(hashMap));
            return ResultUtil.returnSuccess();
        }catch (Exception e){
            log.info("发布消息异常");
            e.printStackTrace();
            //  TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //手动开启事务回滚
            return  ResultUtil.returnError("发布信息异常",500,e);
        }
    }
}
