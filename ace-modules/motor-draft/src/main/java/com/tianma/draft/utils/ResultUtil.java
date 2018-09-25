package com.tianma.draft.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ResultUtil implements Serializable {

    private static final long serialVersionUID = 262888242269244717L;

    //描述
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object content;

    //状态码
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer status = 200;

    //提示信息
    private String errmsg;
    //提示信息
    private String message;

    //异常
    private Exception error;


    //错误返回
    public static ResultUtil returnError(String msg,Integer status,Exception error) {
        ResultUtil result = new ResultUtil();
        result.setStatus(status);
        result.setErrmsg(msg);
        result.setError(error);
        return result;
    }

    //默认错误返回
    public static ResultUtil returnError(String msg) {
        ResultUtil result = new ResultUtil();
        result.setStatus(500);
        result.setErrmsg(msg);
        return result;
    }
    //默认错误返回
    public static ResultUtil returnError(String msg,Integer status) {
        ResultUtil result = new ResultUtil();
        result.setStatus(500);
        result.setErrmsg(msg);
        return result;
    }

    //成功返回
    public static ResultUtil returnSuccess(String msg) {
        ResultUtil result = new ResultUtil();
        result.setStatus(200);
        result.setMessage(msg);
        return result;
    }

    //成功返回
    public static ResultUtil returnSuccess() {
        ResultUtil result = new ResultUtil();
        result.setStatus(200);
        return result;
    }
    //成功返回
    public static ResultUtil returnSuccess(Object object) {
        ResultUtil result = new ResultUtil();
        result.setStatus(200);
        result.setContent(object);
        return result;
    }
}
