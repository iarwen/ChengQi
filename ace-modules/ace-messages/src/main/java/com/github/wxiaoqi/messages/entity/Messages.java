package com.github.wxiaoqi.messages.entity;

import lombok.Data;
import lombok.ToString;

/**
 *  消息实体类
 *  梁健
 */
@Data
@ToString
public class Messages {

    //主键ID
    private Long id;
    //发送时间，由push服务生成
    private String time;
    //消息类型，message: 通知消息， business: 业务消息
    private String type;
    //主送，由push服务分解
    private String[] to;
    //发出人id，由客户端获取详情
    private Long form;
    //业务类型
    private BusinessType business;
    //点击消息时的连接
    private String link;
    //相关的业务标签
    private String[] tags;
    //用户打开过此消息时置位真
    private boolean readed;
    //当前端收到removed为真的消息时要将此id代表的消息从信息流中删除
    private boolean removed = true;
    //消息体
    private MessagesBody body;

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
}
