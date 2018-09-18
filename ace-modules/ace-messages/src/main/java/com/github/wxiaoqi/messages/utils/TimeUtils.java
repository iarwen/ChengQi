package com.github.wxiaoqi.messages.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Auther: liangjian
 * @Date: 2018/9/18 10:57
 * @Description: 生成时间工具类
 */
public  class TimeUtils {

    private static SimpleDateFormat DATEFORMATB_YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getTime(){
        Date date = new Date();
        String time = DATEFORMATB_YYYY_MM_DD_HH_MM_SS.format(date);
        return time;
    };

    //生成时间戳
    public static  String getTimeStamp(){
        Date date = new Date();
        long time = date.getTime();
        return  String.valueOf(time);
    };

}
