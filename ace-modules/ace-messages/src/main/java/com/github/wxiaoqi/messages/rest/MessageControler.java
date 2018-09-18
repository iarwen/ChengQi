package com.github.wxiaoqi.messages.rest;

import com.github.wxiaoqi.messages.entity.Messages;
import com.github.wxiaoqi.messages.service.MessageService;
import com.github.wxiaoqi.messages.service.NewSubscriptionService;
import com.github.wxiaoqi.messages.service.SettingService;
import com.github.wxiaoqi.messages.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 *  梁健
 *  2018/9/17 15:49
 *  消息推送Controller
 */
@Controller
@RequestMapping("/api/v1/")
@Slf4j
public class MessageControler {

    @Autowired
    private MessageService messageService;
    @Autowired
    private NewSubscriptionService newSubscriptionService;
    @Autowired
    private SettingService settingService;

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



















  /**
   *
   * 功能描述: 消息订阅
   *
   * @param:
   * @return:
   * @auther: JJY
   * @date: 2018/9/18
   */
  @RequestMapping(value = "push/{uid}/sub",method = RequestMethod.GET)
  @ResponseBody
  public ResultUtil newSubscription (Long uid){
      log.info("传入参数uid:",uid);
      return newSubscriptionService.newSubscription(uid);
  };
    /**
     *
     * 功能描述: 设置已读
     *
     * @param:
     * @return:
     * @auther: JJY
     * @date: 2018/9/18
     */
    @RequestMapping(value = "push/{uid}/messages/{message_id}/read",method = RequestMethod.GET)
    @ResponseBody
    public ResultUtil settingRead (Long uid,Long message_id){
        log.info("传入参数uid:"+ uid + "信息id:"+ message_id);
        return settingService.settingRead(uid,message_id);
    };
}
