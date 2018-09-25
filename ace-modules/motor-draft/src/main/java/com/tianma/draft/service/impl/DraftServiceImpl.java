package com.tianma.draft.service.impl;

import com.alibaba.fastjson.JSON;
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
     * 功能描述:获取草稿箱列表
     *
     * @param: uid
     * @return:  resultUtil
     * @auther: JJY
     * @date: 2018/9/24
     */
    @Override
    public ResultUtil listDraft(Long uid, Long pageNum, Long pageSize) {
        try {
            log.info("开始查询列表");
            Long start;
            Long stop;
            if (ObjectUtils.isEmpty(uid) || ObjectUtils.isEmpty(pageNum)||ObjectUtils.isEmpty(pageSize)) {
                log.error("传入uid为空或者pageNum为空或者出入pageSize为空");
                ResultUtil.returnError("传入uid为空或者pageNum为空或者出入pageSize为空", 500);
            }
            log.info("=============查询历史信息开始===========");
            start = (pageNum -1) * pageSize  ;
            stop = (pageNum -1) * pageSize  + pageSize - 1 ;
            Set<String> all = jedis.zrange("user:" + uid + ":message:draft", start, stop);
            jedis.close();
            log.info("查询结束");
            return ResultUtil.returnSuccess(all);
        } catch (Exception e) {
            log.info("获取列表异常");
            e.printStackTrace();
            return  ResultUtil.returnError("获取列表异常异常",500,e);
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
