package com.github.wxiaoqi.messages.rest;

import com.github.wxiaoqi.messages.entity.Messages;
import com.github.wxiaoqi.messages.service.MessageService;
import com.github.wxiaoqi.messages.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

/**
 *  梁健
 *  2018/9/17 15:49
 *  消息推送Controller
 */
@Controller
@RequestMapping("/api/v1/")
public class MessageControler {

    @Autowired
    private MessageService messageService;

    /**
     * 发布消息
     * 梁健
     * 2018/09/17 15:45
     * @param messages
     */
    @RequestMapping(value = "push",method = RequestMethod.POST)
    @ResponseBody
    public ResultUtil releaseTheMessage(@RequestBody Messages messages){
        return messageService.releaseTheMessage(messages);
    };

}
