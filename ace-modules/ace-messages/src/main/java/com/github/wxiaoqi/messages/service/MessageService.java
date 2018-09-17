package com.github.wxiaoqi.messages.service;

import com.github.wxiaoqi.messages.entity.Messages;
import com.github.wxiaoqi.messages.utils.ResultUtil;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

    /**
     * 发布消息
     * @param messages
     * @return
     */
    ResultUtil releaseTheMessage(Messages messages);
}
