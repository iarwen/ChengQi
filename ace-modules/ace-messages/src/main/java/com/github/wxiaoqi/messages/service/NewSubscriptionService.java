package com.github.wxiaoqi.messages.service;

import com.github.wxiaoqi.messages.utils.ResultUtil;
import org.springframework.stereotype.Service;

/**
 * @Auther: JJY
 * @Date: 2018/9/18 10:49
 * @Description:
 */
@Service
public interface NewSubscriptionService {

    ResultUtil newSubscription(Long uid);
}
