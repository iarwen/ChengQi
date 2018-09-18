package com.github.wxiaoqi.messages.service;

import com.github.wxiaoqi.messages.utils.ResultUtil;
import org.springframework.stereotype.Service;

/**
 * @Auther: JJY
 * @Date: 2018/9/18 15:19
 * @Description:
 */
@Service
public interface SettingService {

    ResultUtil settingRead(Long uid, Long message_id);
}
