package com.github.wxiaoqi.messages.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessagesBody {

    //表单id
    private Long businessKey;

    // '标题',
    private String title;

    //内容摘要
    private String content;

    //可选，附件
    private Object[] attachments;

    //可选，直接在信息流中处理业务的动作
    private Object[] actions;

}
