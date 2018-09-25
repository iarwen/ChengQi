package com.tianma.draft.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tianma.draft.config.RedisConfig;
import com.tianma.draft.entity.Messages;
import com.tianma.draft.service.DraftService;
import com.tianma.draft.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * @Auther: JJY
 * @Date: 2018/9/24 22:29
 * @Description:
 */
@Service
@Slf4j
public class DraftServiceImpl implements DraftService {


    private Jedis jedis = new RedisConfig().jedisTemplate(DraftServiceImpl.class);

    /**
     * 功能描述: 添加草稿信息
     *
     * @param: messages uid
     * @return: ResultUtil
     * @auther: JJY
     * @date: 2018/9/24
     */
    @Override
    public ResultUtil addDraft(Messages messages, Long uid) {
        try {
            log.info("传入的参数为：" + JSON.toJSONString(messages));
            if (ObjectUtils.isEmpty(messages) || Objects.isNull(messages) || ObjectUtils.isEmpty(uid)) {
                return ResultUtil.returnError("消息体为空无法保存");
            }
            log.info("开始生成 消息ID 和 发送时间");
            Long incrId = getIncrId();
            messages.setId(incrId);
            log.info("生成的消息ID为：" + messages.getId());
            log.info("消息发送的时间为：" + messages.getTime());
            jedis.zadd("user:" + uid + ":message:draft", incrId, JSON.toJSONString(messages));
            log.info("消息已存入草稿箱列表 : user:" + uid + ":message:draft");
            jedis.close();
            log.info("关闭redis");
            return ResultUtil.returnSuccess();
        } catch (Exception e) {
            log.info("发布消息异常");
            e.printStackTrace();
            //  TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); //手动开启事务回滚
            return ResultUtil.returnError("发布信息异常", 500, e);
        }
    }

    /**
     * 功能描述:删除草稿箱某条信息
     *
     * @param: message_id uid
     * @return:  resultUtil
     * @auther: JJY
     * @date: 2018/9/24
     */
    @Override
    public ResultUtil deleteDraft(Long message_id, Long uid) {

        try {
            log.info("传入的参数为：" + message_id + uid);
            if (ObjectUtils.isEmpty(message_id) || ObjectUtils.isEmpty(uid)) {
                return ResultUtil.returnError("传入信息id或者uid为空");
            }
            log.info("开始删除信息");
            Set<String> zrange = jedis.zrange("user:" + uid + ":message:draft", 0, -1);
            log.info("Redis 待转的数据为 ： "+ zrange);
            Iterator<String> iterator = zrange.iterator();
            while (iterator.hasNext()){
                Messages messages = JSON.parseObject(iterator.next(),Messages.class);
                if(messages.getId() == message_id){
                    //获取score
                    Double zscore = jedis.zscore("user:" + uid + ":message:draft", JSON.toJSONString(messages));
                    log.info("score:" + zscore);
                    log.info("删除草稿箱信息：" + JSON.toJSON(messages));
                    jedis.zrem("user:" + uid + ":message:draft", JSON.toJSONString(messages));
                    jedis.close();
                    break;
                }
            }
            return ResultUtil.returnSuccess("删除草稿箱信息成功");
        } catch (Exception e) {
            log.info("删除草稿箱信息异常");
            e.printStackTrace();
            return ResultUtil.returnError("删除草稿箱信息异常", 500, e);
        }
    }

    /**
     * 功能描述:提取草稿箱的某条信息
     *
     * @param: message_id uid
     * @return:  resultUtil
     * @auther: 梁健
     * @date: 2018年9月25日09:04:14
     */
    @Override
    public ResultUtil getDratft(Long message_id, Long uid) {
        Object str = null;
        try{
            log.info("开始提取草稿箱信息");
            log.info("开始检查参数");
            if(ObjectUtils.isEmpty(message_id) || ObjectUtils.isEmpty(uid)
                    || Objects.isNull(message_id) || Objects.isNull(uid)){
             return ResultUtil.returnError("参数异常，请检查参数",500);
            }

            String redisKey = "user:" + uid + ":message:draft";
            log.info("当前操作的 key 为 ： "+redisKey);
            Set<String> strings = jedis.zrangeByScore(redisKey, message_id, message_id);
            for (String strs : strings){
                str = strs;
            }
            log.info("获取的草稿信息为： "+str);
            log.info("草稿箱信息提取完成");
            return  ResultUtil.returnSuccess(str);
        }catch (Exception e){
            e.printStackTrace();
            log.info("获取某条草稿信息异常");
            return ResultUtil.returnError("草稿箱提取异常",500,e);
        }

    }

    /***
     *  getIncrId
     * @return
     */
    public Long getIncrId() {
        return jedis.incr("mortor");
    }
}
