package com.github.wxiaoqi.messages.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.messages.entity.Messages;
import com.github.wxiaoqi.messages.entity.MessagesBody;
import com.github.wxiaoqi.messages.service.MessageService;
import com.github.wxiaoqi.messages.utils.ResultUtil;
import com.github.wxiaoqi.messages.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;
import sun.plugin2.message.Message;

import java.util.*;

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
     * 功能描述:
     * @param: [messages]
     * @auther:  梁健
     * @date: 2018/9/18 9:09
     * @description: 发布消息
     * @return:
     */
    @Override
    public ResultUtil releaseTheMessage(Messages messages) {
        try{
            log.info("传入的参数为："+JSON.toJSONString(messages));
            if(ObjectUtils.isEmpty(messages) || Objects.isNull(messages)){
             return ResultUtil.returnError("消息体为空无法发布");
            };
            log.info("开始生成 消息ID 和 发送时间");
            String time = TimeUtils.getTimeStamp();
            int radomNumber = (int)((Math.random()*9+1)*100000);
            messages.setId(messages.getType().toUpperCase()+""+time+""+radomNumber);
            messages.setTime(TimeUtils.getTime());
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
            log.info("主送为：", JSON.toJSONString(hashMap));
            if(ObjectUtils.isEmpty(hashMap) || Objects.isNull(hashMap)){
                ResultUtil.returnError("to 为空发布失败");
            }

            log.info("开始将消息存入消息列表");
            boolean flag1 = saveMessageInRedis(messages,hashMap);
            if(!flag1){
                return  ResultUtil.returnError("消息存入消息列表失败");
            }
            log.info("开始将消息存入消息列表完成,开始将消息存入Channel");
            boolean flag2 = saveMessageInChannel(messages, hashMap);
            if(flag2){
                log.info("消息存入管道失败,开始清除Redis信息");
                String msg = messages.getType().equals("message")== true ? "message": "todo";
                jedis.zrem("user:" +  hashMap.get("user")+ ":"+msg+":zset",JSON.toJSONString(messages));
                jedis.close();
                return ResultUtil.returnError("消息存入管道失败");
            }
            log.info("Channel存储完毕");
            return ResultUtil.returnSuccess();
        }catch (Exception e){
            log.info("发布消息异常");
            e.printStackTrace();
            //  TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //手动开启事务回滚
            return  ResultUtil.returnError("发布信息异常",500,e);
        }finally {
            jedis.close();
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
            log.info("开始转 已办");
            String uid = hashMap.get("uid").toString();
            Set<String> zrange = jedis.zrange("user:" + uid + ":todo:zset", 0, -1);
            log.info("Redis 待转的数据为 ： "+ zrange);
            Iterator<String> iterator = zrange.iterator();
            if(iterator.hasNext() == false) return  ResultUtil.returnError("暂无消息可以转 已办");
            while (iterator.hasNext()){
                Messages  messages = JSON.parseObject(iterator.next(),Messages.class);
                if(messages.isRemoved() == false){
                    return  ResultUtil.returnError("该消息已经被转办");
                }else if (messages.getId().equals(hashMap.get("message_id"))){
                    log.info("开始删除待转办的数据："+JSON.toJSON(messages));
                    jedis.zrem("user:" + uid + ":todo:zset",JSON.toJSONString(messages));
                    log.info("删除完毕，开始查询已办列表信息");
                    Object[] objects = jedis.zrevrange("user:" +  hashMap.get("uid")+ ":done:zset", 0, -1).toArray();
                    Double userCount;
                    if (objects.length == 0) {
                        userCount = 0.00;
                    }else {
                        userCount = jedis.zscore("user:" + hashMap.get("uid") + ":done:zset",objects[0].toString());
                    }
                    log.info("当前已办消息列表中有： "+userCount+" 条数据");

                    log.info("开始添加已办信息列表");
                    jedis.zadd("user:"+hashMap.get("uid")+":done:zset",Double.valueOf(userCount+1),JSON.toJSONString(messages));
                    log.info("转已办完毕,将消息存入 channel");
                    hashMap.put("user",hashMap.get(uid));
                    boolean flag = saveMessageInChannel(messages, hashMap);
                    if(flag == false){
                        jedis.close();
                        return ResultUtil.returnError("消息存入 user:"+hashMap.get("uid")+":done:channel  失败");
                    }
                    jedis.close();
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
            log.info("开始转 已办");
            String uid = hashMap.get("uid").toString();
            Set<String> zrange = jedis.zrange("user:" + uid + ":todo:zset", 0, -1);
            log.info("Redis 待转的数据为 ： "+ zrange);
            Iterator<String> iterator = zrange.iterator();
            if(iterator.hasNext() == false) return  ResultUtil.returnError("暂无消息可以转 已办");
            while (iterator.hasNext()){
                Messages  messages = JSON.parseObject(iterator.next(),Messages.class);
                Long businessKey = messages.getBody().getBusinessKey();
                if(messages.isRemoved() == false){
                    return  ResultUtil.returnError("该消息已经被转办");
                }else if ((businessKey+"").equals(hashMap.get("businessKey"))){

                    log.info("删除完毕，开始查询已办列表信息");

                    Object[] objects = jedis.zrevrange("user:" +  hashMap.get("uid")+ ":done:zset", 0, -1).toArray();
                    Double userCount;
                    if (objects.length == 0) {
                        userCount = 0.00;
                    }else {
                        userCount = jedis.zscore("user:" + hashMap.get("uid") + ":done:zset",objects[0].toString());
                    }

                    log.info("开始删除待转办的数据："+JSON.toJSON(messages));
                    jedis.zrem("user:" + uid + ":todo:zset",JSON.toJSONString(messages));

                    log.info("开始添加已办信息列表");
                    jedis.zadd("user:"+hashMap.get("uid")+":done:zset",Double.valueOf(userCount+1),JSON.toJSONString(messages));
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
            //在redis中添加消息列表
            if("message".equals(messages.getType())){
                //查询Redis中的消息数量
                Object[] objects = jedis.zrevrange("user:" +  hashMap.get("user")+ ":message:zset", 0, -1).toArray();
                Double userCount;
                if (objects.length == 0) {
                    userCount = 0.00;
                }else {
                    userCount = jedis.zscore("user:" + hashMap.get("user") + ":message:zset", JSON.toJSONString(objects[0]));
                }
                //通知消息列表
                jedis.zadd("user:"+hashMap.get("user")+":message:zset",Double.valueOf(userCount+1),JSON.toJSONString(messages));
            }else if("business".equals(messages.getType())){
                //查询Redis中的消息数量
                Object[] objects = jedis.zrevrange("user:" +  hashMap.get("user")+ ":todo:zset", 0, -1).toArray();
                Double userCount;
                if (objects.length == 0) {
                    userCount = 0.00;
                }else {
                    userCount = jedis.zscore("user:" + hashMap.get("user") + ":todo:zset",objects[0].toString());
                }
                //代办消息列表
                jedis.zadd("user:"+hashMap.get("user")+":todo:zset",Double.valueOf(userCount+1),JSON.toJSONString(messages));
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
     * @param: Messages
     * @auther: 梁健
     * @date: 2018/9/18 9:42
     * @description: 将消息存入管道
     * @return: boolean
     */
    public boolean saveMessageInChannel(Messages messages,Map hashMap){
        try {
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

}
