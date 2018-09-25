package com.github.wxiaoqi.messages.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.messages.config.RedisConfig;
import com.github.wxiaoqi.messages.entity.Messages;
import com.github.wxiaoqi.messages.service.MessageService;
import com.github.wxiaoqi.messages.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 *  梁健
 *  2018/9/17 15:49
 *  消息推送Service
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {



    /**
     * 功能描述:
     * @param: [messages]
     * @auther:  梁健
     * @date: 2018/9/18 9:09
     * @description: 发布消息
     * @return:
     */
    @Override
    public ResultUtil releaseTheMessage(Messages messages) {
        Jedis jedis =  new RedisConfig().jedisTemplate(MessageServiceImpl.class);
        try{
            log.info("传入的参数为："+JSON.toJSONString(messages));
            if(ObjectUtils.isEmpty(messages) || Objects.isNull(messages)){
             return ResultUtil.returnError("消息体为空无法发布");
            };
            log.info("开始生成 消息ID 和 发送时间");
            Long incrId = getIncrId();
            messages.setId(incrId);
            log.info("生成的消息ID为："+messages.getId());
            log.info("消息发送的时间为："+messages.getTime());

            log.info("开始抽取主送消息");
            HashMap<String, Object> hashMap = new HashMap<>(16);
            String[] to = messages.getTo();
            //抽取 主送信息
            for (String str : to) {
                String[] split = str.split(":");
                hashMap.put(split[0], split[1]);
            }
           // log.info("主送为：", JSON.toJSONString(hashMap));
            if(ObjectUtils.isEmpty(hashMap) || Objects.isNull(hashMap)){
                ResultUtil.returnError("to 为空发布失败");
            }

            log.info("开始将消息存入消息列表");
            if (!"message".equals(messages.getType()) && !"business".equals(messages.getType())) {
                log.info("未知的消息类型: "+messages.getType());
                return ResultUtil.returnError("未知消息类型: "+messages.getType(),404);
            }

            boolean flag1 = saveMessageInRedis(messages,hashMap);
            if(!flag1){
                return  ResultUtil.returnError("消息存入消息列表失败");
            }
            log.info("存入消息列表完成,开始将消息存入Channel");
            boolean flag2 = saveMessageInChannel(messages, hashMap);
            if(!flag2){
                log.info("消息存入管道失败,开始清除Redis信息");
                String msg = messages.getType().equals("message")== true ? "message": "todo";
                jedis.zrem("user:" +  hashMap.get("user")+ ":"+msg+":zset",JSON.toJSONString(messages));
                jedis.close();
                log.info("Redis 连接关闭");
                return ResultUtil.returnError("消息存入管道失败");
            }
            log.info("Channel存储完毕");
            return ResultUtil.returnSuccess();
        }catch (Exception e){
            log.info("发布消息异常");
            e.printStackTrace();
            //  TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //手动开启事务回滚
            return  ResultUtil.returnError("发布信息异常",500,e);
        }
    }

    /**
     *
     * 功能描述: 
     *
     * @param: hashMap
     * @auther: 梁建
     * @date: 2018/9/18 13:40
     * @description:  转已办
     * @return: ResultUtil
     */
    @Override
    public ResultUtil agencyToHaveDone(Map hashMap) {
        try {
            Jedis jedis =  new RedisConfig().jedisTemplate(MessageServiceImpl.class);
            log.info("开始验证参数");
            if(Objects.isNull(hashMap) || ObjectUtils.isEmpty(hashMap) ||
                    ObjectUtils.isEmpty(hashMap.get("uid").toString()) || Objects.isNull(hashMap.get("uid")) ||
                    ObjectUtils.isEmpty(hashMap.get("message_id").toString()) || Objects.isNull(hashMap.get("message_id"))
                    ){
                return  ResultUtil.returnError("参数验证失败,请检查参数",500);
            }
            log.info("参数验证完成");

            log.info("开始转 已办");
            String uid = hashMap.get("uid").toString();
            Set<String> zrange = jedis.zrange("user:" + uid + ":todo:zset", 0, -1);
            Iterator<String> iterator = zrange.iterator();
            if(iterator.hasNext() == false) return  ResultUtil.returnError("暂无消息可以转 已办");
            while (iterator.hasNext()){
                Messages  messages = JSON.parseObject(iterator.next(),Messages.class);
                if (messages.getId().toString().equals(hashMap.get("message_id"))){
                    if(messages.isRemoved() == false){
                        log.info("数据异常，代办列表中出现已办数据，请检查代办数据列表");
                        return  ResultUtil.returnError("该消息为已办数据,不可再 转 已办");
                    }
                    log.info("Redis 待转的数据为 ： "+ messages);
                    log.info("开始删除待转办的数据："+JSON.toJSON(messages));
                    jedis.zrem("user:" + uid + ":todo:zset",JSON.toJSONString(messages));
                    log.info("删除完毕，开始查询已办列表信息");
                    log.info("开始添加已办信息列表");
                    jedis.zadd("user:"+hashMap.get("uid")+":done:zset",messages.getId(),JSON.toJSONString(messages));
                    log.info("转已办完毕,将消息存入 channel");
                    hashMap.put("user",hashMap.get(uid));
                    boolean flag = saveMessageInChannel(messages, hashMap);
                    if(flag == false){
                        jedis.close();
                        log.info("Redis 连接关闭");
                        return ResultUtil.returnError("消息存入 user:"+hashMap.get("uid")+":done:channel  失败");
                    }
                    jedis.close();
                    log.info("Redis 连接关闭");
                }
            }
            return ResultUtil.returnSuccess();
        }catch (Exception e){
            log.info("转已办异常");
            e.printStackTrace();
            return  ResultUtil.returnError("发布信息异常",500,e);
        }
    }


    /**
     *
     * 功能描述:
     *
     * @param: hashMap
     * @auther: 梁建
     * @date: 2018/9/18 13:00
     * @description: 转已办
     * @return: status
     */
    @Override
    public ResultUtil agencyToHaveDoneByBusinessKey(HashMap<String, Object> hashMap) {
        try {
            Jedis jedis =  new RedisConfig().jedisTemplate(MessageServiceImpl.class);
            log.info("开始验证参数");
            if(Objects.isNull(hashMap) || ObjectUtils.isEmpty(hashMap) ||
               ObjectUtils.isEmpty(hashMap.get("uid").toString()) || Objects.isNull(hashMap.get("uid")) ||
               ObjectUtils.isEmpty(hashMap.get("businessKey").toString()) || Objects.isNull(hashMap.get("businessKey"))
                    ){
                return  ResultUtil.returnError("参数验证失败,请检查参数",500);
            }
            log.info("参数验证完成");

            log.info("开始转 已办");
            String uid = hashMap.get("uid").toString();
            Set<String> zrange = jedis.zrange("user:" + uid + ":todo:zset", 0, -1);
            log.info("Redis 待转的数据为 ： "+ zrange);
            Iterator<String> iterator = zrange.iterator();
            if(iterator.hasNext() == false) return  ResultUtil.returnError("暂无消息可以转 已办");
            while (iterator.hasNext()){
                Messages  messages = JSON.parseObject(iterator.next(),Messages.class);
                Long businessKey = messages.getBody().getBusinessKey();
                if ((businessKey+"").equals(hashMap.get("businessKey"))){
                    if(messages.isRemoved() == false){
                        log.info("数据异常，代办列表中出现已办数据，请检查代办数据列表");
                        return  ResultUtil.returnError("该消息为已办数据,不可再 转 已办");
                    }

                    log.info("开始删除待转办的数据："+JSON.toJSON(messages));
                    jedis.zrem("user:" + uid + ":todo:zset",JSON.toJSONString(messages));

                    log.info("开始添加已办信息列表");
                    jedis.zadd("user:"+hashMap.get("uid")+":done:zset",messages.getId(),JSON.toJSONString(messages));
                    log.info("已办信息列表添加完成");

                    log.info("开始将消息存入 Chnnal");
                    hashMap.put("user",uid);
                    boolean flag = saveMessageInChannel(messages, hashMap);
                    if(flag == false){
                        return ResultUtil.returnError("消息存入 user:"+hashMap.get("uid")+":done:channel  失败");
                    }
                    log.info("消息存入 Chnnal 完成");
                }
            }
            jedis.close();
            log.info("Redis 连接关闭");
            return ResultUtil.returnSuccess();
        }catch (Exception e){
            log.info("转已办异常");
            e.printStackTrace();
            return  ResultUtil.returnError("发布信息异常",500,e);
        }
    }


    /**
     *
     * 功能描述:
     * @param: Messages
     * @auther: 梁健
     * @date: 2018/9/18 9:42
     * @description: 将消息存储到消息列表
     * @return: boolean
     */
    public boolean saveMessageInRedis(Messages messages, Map hashMap){
        try {
            Jedis jedis =  new RedisConfig().jedisTemplate(MessageServiceImpl.class);
            //在redis中添加消息列表
            if("message".equals(messages.getType())){
                //通知消息列表
                jedis.zadd("user:"+hashMap.get("user")+":message:zset",messages.getId(),JSON.toJSONString(messages));
                log.info("消息已存入通知消息列表 : user:"+hashMap.get("user")+":message:zset");
            }else if("business".equals(messages.getType())){
                //判断数据是否是已办数据
                if(messages.isRemoved() == false){
                    log.info("已办消息,不可存入待办消息记录列表。");
                    return false;
                }else {
                    //代办消息列表
                    jedis.zadd("user:"+hashMap.get("user")+":todo:zset",messages.getId(),JSON.toJSONString(messages));
                    log.info("消息已存入代办消息列表 : user:"+hashMap.get("user")+":todo:zset");
                }
            }else{
                log.info("未知的消息类型: "+messages.getType());
                return false;
            }
            jedis.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    };
    /**
     *
     * 功能描述:
     * @param: Messages hashMap
     * @auther: 梁健
     * @date: 2018/9/18 9:42
     * @description: 将消息存入管道
     * @return: boolean
     */
    public boolean saveMessageInChannel(Messages messages,Map hashMap){
        try {
            Jedis jedis =  new RedisConfig().jedisTemplate(MessageServiceImpl.class);
            //查询Redis中的消息数量
            Long userCount = jedis.zcard("user");
            //在频道中添加消息
            if("message".equals(messages.getType())){
                jedis.publish("user:"+hashMap.get("user")+":message:channel",JSON.toJSONString(messages));
                log.info("已放入 消息通知 Channel : "+"user:"+hashMap.get("user")+":message:channel");
            }else if("business".equals(messages.getType())){
                if(messages.isRemoved() == false){
                    jedis.publish("user:"+hashMap.get("user")+":done:channel",JSON.toJSONString(messages));
                    log.info("已放入已办 Channel : "+"user:"+hashMap.get("user")+":done:channel");
                }else if (messages.isRemoved() == true){
                    jedis.publish("user:"+hashMap.get("user")+":todo:channel",JSON.toJSONString(messages));
                    log.info("已放入待办 Channel : "+"user:"+hashMap.get("user")+":todo:channel");
                }else {
                    log.info("未知消息无法放入 Channel");
                    return false;
                }
            }
            jedis.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    };

    /***
     *  getIncrId
     * @return
     */
    public Long getIncrId(){
        Jedis jedis =  new RedisConfig().jedisTemplate(MessageServiceImpl.class);
        return jedis.incr("mortor");
    }

}
