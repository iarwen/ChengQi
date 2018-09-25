package com.github.wxiaoqi.messages.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @Auther: liangjian
 * @Date: 2018/9/25 18:14
 * @Description:
 */
@Data
@ToString
public class UserInfo {
    private Long id;
    private String name;
    private String avatar;
    private String title;
    private String link;
}
