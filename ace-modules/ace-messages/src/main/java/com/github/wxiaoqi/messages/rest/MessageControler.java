package com.github.wxiaoqi.messages.rest;

import com.github.wxiaoqi.messages.entity.Messages;
import com.github.wxiaoqi.messages.service.MessageService;
import com.github.wxiaoqi.messages.service.NewSubscriptionService;
import com.github.wxiaoqi.messages.service.SettingService;
import com.github.wxiaoqi.messages.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

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
    @RequestMapping(value = "/push",method = RequestMethod.POST)
    @ResponseBody
    public ResultUtil releaseTheMessage(@RequestBody Messages messages){
        return messageService.releaseTheMessage(messages);
    };


    /**
     *
     * 功能描述:
     *
     * @param:
     * @return:
     * @auther: 梁建
     * @date: 2018/9/18 13:00
     * @description:
     * @return:
     */
    @RequestMapping(value = "/push/{uid}/messages/{message_id}/done",method = RequestMethod.GET,produces="application/json;charset=UTF-8")
    @ResponseBody
    public  ResultUtil agencyToHaveDone(@PathVariable Long uid,@PathVariable String message_id){
        System.out.println("uid ---> "+uid);
        System.out.println("message_id ---> "+message_id);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("message_id",message_id);
        return messageService.agencyToHaveDone(hashMap);
    };


    /**
     *
     * 功能描述:
     *
     * @param:
     * @return:
     * @auther: 梁建
     * @date: 2018/9/18 13:00
     * @description:
     * @return:
     */
    @RequestMapping(value = "/push/{uid}/messages/forms/{businessKey}/done",method = RequestMethod.GET,produces="application/json;charset=UTF-8")
    @ResponseBody
    public  ResultUtil agencyToHaveDoneByBusinessKey(@PathVariable Long uid,@PathVariable String businessKey){
        log.info("入参为：    uid = "+uid+"  businessKey = "+businessKey);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("businessKey",businessKey);
        return messageService.agencyToHaveDoneByBusinessKey(hashMap);
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
  public ResultUtil newSubscription (@PathVariable Long uid){

      log.info("传入参数uid:",uid);   return newSubscriptionService.newSubscription(uid);
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
    public ResultUtil settingRead (@PathVariable Long uid,@PathVariable Long message_id){
        log.info("传入参数uid:"+ uid + "信息id:"+ message_id);
        return settingService.settingRead(uid,message_id);
    };
    /**
     *
     * 功能描述: 全部置已读
     *
     * @param:
     * @return:
     * @auther: JJY
     * @date: 2018/9/18
     */
    @RequestMapping(value = "push/{uid}/messages/all_read",method = RequestMethod.GET)
    @ResponseBody
    public ResultUtil settingAllRead (@PathVariable Long uid, Long pageNum, Long pageSize){
        log.info("传入参数uid:"+ uid + "页数:"+ pageNum +"条数:"+ pageSize);
        return settingService.settingAllRead(uid,pageNum,pageSize);
    };
    /**
     *
     * 功能描述: 全部置已读
     *
     * @param:
     * @return:
     * @auther: JJY
     * @date: 2018/9/18
     */
    @RequestMapping(value = "push/{uid}/list",method = RequestMethod.GET)
    @ResponseBody
    public ResultUtil settingAllRead (@PathVariable Long uid,Long message, Long pageNum, Long pageSize){
        log.info("传入参数uid:"+ uid + "页数:"+ pageNum +"条数:"+ pageSize);
        return settingService.settingList(uid,pageNum,pageSize);
    };


}
