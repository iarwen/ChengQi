package com.github.wxiaoqi.messages.service;

import com.github.wxiaoqi.messages.entity.Messages;
import com.github.wxiaoqi.messages.utils.ResultUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public interface MessageService {

    /**
     * 发布消息
     * @param messages
     * @return
     */
    ResultUtil releaseTheMessage(Messages messages);

    /**
     *
     * 功能描述:
     *
     * @param: hashMap
     * @return:
     * @auther: 1
     * @date: 2018/9/18 13:00
     * @description: 转已办
     * @return: status
     */
    ResultUtil agencyToHaveDone(Map hashMap);

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
    ResultUtil agencyToHaveDoneByBusinessKey(HashMap<String, Object> hashMap);
}
