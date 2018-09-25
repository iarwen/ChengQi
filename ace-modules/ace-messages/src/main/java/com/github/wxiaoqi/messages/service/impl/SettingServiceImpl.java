package com.github.wxiaoqi.messages.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.messages.config.RedisConfig;
import com.github.wxiaoqi.messages.entity.Messages;
import com.github.wxiaoqi.messages.service.SettingService;
import com.github.wxiaoqi.messages.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;

import java.util.Iterator;
import java.util.Set;

/**
 * @Auther: JJY
 * @Date: 2018/9/18 15:20
 * @Description:设置已读
 */
@Slf4j
@Service
public class SettingServiceImpl implements SettingService {


    /**
     * 功能描述: 设置已读
     *
     * @param: uid message_id
     * @return: Result
     * @auther: JJY
     * @date: 2018/9/18
     */
    @Override
    public ResultUtil settingRead(Long uid, String message_id) {
        try {
         Jedis jedis =  new RedisConfig().jedisTemplate(NewSubscriptionServiceImpl.class);
        if (ObjectUtils.isEmpty(uid) || ObjectUtils.isEmpty(message_id)) {
            log.error("传入uid为空或者message_id为空");
            ResultUtil.returnError("传入uid为空或者message_id为空", 500);
        }
        log.info("=============设置已读开始===========");
            Set<String> zrange = jedis.zrange("user:" + uid + ":message:zset", 0, -1);
            log.info("Redis 待转的数据为 ： "+ zrange);
            Iterator<String> iterator = zrange.iterator();
            while (iterator.hasNext()){
                Messages messages = JSON.parseObject(iterator.next(),Messages.class);
                if(messages.getId().toString().equals(message_id)){
                        //获取score
                        Double zscore = jedis.zscore("user:" + uid + ":message:zset", JSON.toJSONString(messages));
                        log.info("score:" + zscore);
                        log.info("开始删除待处理的数据：" + JSON.toJSON(messages));
                        jedis.zrem("user:" + uid + ":message:zset", JSON.toJSONString(messages));
                        //修改为已读
                        messages.setReaded(true);
                        log.info("重新插入处理后信息");
                        jedis.zadd("user:" + uid + ":message:zset", zscore, JSON.toJSONString(messages));
                        log.info("==========插入成功========");
                        jedis.close();
                        break;
                    }
                }
            return ResultUtil.returnSuccess("设置已读成功");
        } catch (Exception e) {
            log.info("设置已读异常");
            e.printStackTrace();
            return  ResultUtil.returnError("设置已读异常",500,e);
        }
    }
    /**
     * 功能描述: 设置全部已读
     *
     * @param: uid message_id
     * @return: Result
     * @auther: JJY
     * @date: 2018/9/18
     */
    @Override
    public ResultUtil settingAllRead(Long uid, Long pageNum, Long pageSize) {

        try {
            Jedis jedis =  new RedisConfig().jedisTemplate(NewSubscriptionServiceImpl.class);
            Long start;
            Long stop;
            if (ObjectUtils.isEmpty(uid) || ObjectUtils.isEmpty(pageNum)||ObjectUtils.isEmpty(pageSize)) {
                log.error("传入uid为空或者pageNum为空或者出入pageSize为空");
                ResultUtil.returnError("传入uid为空或者pageNum为空或者出入pageSize为空", 500);
            }
            log.info("=============设置全部已读开始===========");
            start = (pageNum -1) * pageSize  ;
            stop = (pageNum -1) * pageSize  + pageSize - 1;
            Set<String> zrange = jedis.zrange("user:" + uid + ":message:zset", start, stop);
            log.info("Redis 待转的数据为 ： "+ zrange);
            //迭代器
            Iterator<String> iterator = zrange.iterator();
            while (iterator.hasNext()){
                Messages messages = JSON.parseObject(iterator.next(),Messages.class);
                if(messages.isReaded() == true){
                    ResultUtil.returnError("该消息已读");
                }else{
                    //分别获取所有message的score
                    Double score = jedis.zscore("user:" + uid + ":message:zset",JSON.toJSONString(messages));
                    log.info("===========score:"+score);
                    log.info("开始删除待处理的数据："+JSON.toJSON(messages));
                    jedis.zrem("user:" + uid + ":message:zset",JSON.toJSONString(messages));
                    //修改为已读
                    messages.setReaded(true);
                    log.info("重新插入处理后信息");
                    jedis.zadd("user:"+uid+":message:zset",score, JSON.toJSONString(messages));
                    log.info("==========插入成功========");
                }


            }
            Set<String> all = jedis.zrange("user:" + uid + ":message:zset", start, stop);
            jedis.close();
            return ResultUtil.returnSuccess(all);
        } catch (Exception e) {
            log.info("设置已读异常");
            e.printStackTrace();
            return  ResultUtil.returnError("设置已读异常",500,e);
    }
}
    /**
     * 功能描述: 获取历史列表
     *
     * @param: uid
     * @return: Result
     * @auther: JJY
     * @date: 2018/9/18
     */
    @Override
    public ResultUtil settingList(Long uid, Long pageNum, Long pageSize) {
        try {
            Jedis jedis =  new RedisConfig().jedisTemplate(NewSubscriptionServiceImpl.class);
            Long start;
            Long stop;
            if (ObjectUtils.isEmpty(uid) || ObjectUtils.isEmpty(pageNum)||ObjectUtils.isEmpty(pageSize)) {
                log.error("传入uid为空或者pageNum为空或者出入pageSize为空");
                ResultUtil.returnError("传入uid为空或者pageNum为空或者出入pageSize为空", 500);
            }
            log.info("=============查询历史信息开始===========");
            start = (pageNum -1) * pageSize  ;
            stop = (pageNum -1) * pageSize  + pageSize - 1 ;
            Set<String> all = jedis.zrange("user:" + uid + ":message:zset", start, stop);
            jedis.close();
            return ResultUtil.returnSuccess(all);
        } catch (Exception e) {
            log.info("设置已读异常");
            e.printStackTrace();
            return  ResultUtil.returnError("设置已读异常",500,e);
        }
    }
}
