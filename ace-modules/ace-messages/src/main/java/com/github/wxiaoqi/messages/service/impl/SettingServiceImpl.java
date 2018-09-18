package com.github.wxiaoqi.messages.service.impl;

import com.github.wxiaoqi.messages.service.SettingService;
import com.github.wxiaoqi.messages.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

/**
 * @Auther: JJY
 * @Date: 2018/9/18 15:20
 * @Description:设置已读
 */
@Slf4j
@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    private Jedis jedis;
    /**
     * 功能描述: 设置已读
     *
     * @param: uid message_id
     * @return: Result
     * @auther: JJY
     * @date: 2018/9/18
     */
    @Override
    public ResultUtil settingRead(Long uid, Long message_id) {
        return null;
    }
}
