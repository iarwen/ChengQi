package com.tianma.draft.rest;

import com.tianma.draft.entity.Messages;
import com.tianma.draft.service.DraftService;
import com.tianma.draft.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: JJY
 * @Date: 2018/9/24 21:48
 * @Description:
 */
@Controller
@RequestMapping("/api/v1/")
@Slf4j
public class DraftControler {

    @Autowired
    private DraftService draftService;
    /**
     * 保存到草稿箱
     * jjy
     * 2018/09/24 15:45
     * @param messages uid
     */
    @RequestMapping(value = "/users/{uid}/draft",method = RequestMethod.POST)
    @ResponseBody
    public ResultUtil addDraft(@RequestBody Messages messages,
                               @PathVariable Long uid){
        return draftService.addDraft(messages,uid);
    };
    /**
     *
     * 功能描述: 删除某条草稿箱信息
     *
     * @param:
     * @return:
     * @auther: JJY
     * @date: 2018/9/24
     */
    @RequestMapping(value = "/users/{uid}/draft/{message_id}",method = RequestMethod.DELETE)
    @ResponseBody
    public ResultUtil deleteDraft(@PathVariable Long message_id,
                               @PathVariable Long uid){
        return draftService.deleteDraft(message_id,uid);
    };

    /**
     *
     * 功能描述: 删除某条草稿箱信息
     *
     * @param:
     * @return:
     * @auther: JJY
     * @date: 2018/9/24
     */
    @RequestMapping(value = "/users/{uid}/draft/{message_id}",method = RequestMethod.GET)
    @ResponseBody
    public ResultUtil getDratft(@PathVariable Long message_id,@PathVariable Long uid){
        return draftService.getDratft(message_id,uid);
    };


}
