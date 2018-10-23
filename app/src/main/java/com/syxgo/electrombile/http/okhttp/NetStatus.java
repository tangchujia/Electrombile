package com.syxgo.electrombile.http.okhttp;

/**
 * @Author: Huangweicai
 * @date 2016-08-24 16:25
 * @Description:网络状态
 */
public class NetStatus {

    public static String localAddr = "";

    /** 消息被接收并解析成功。设备或网关接收到该确认代码后，需要再次传送设备的最新变动数据。主动权在平台*/
    public static String CONT = "CONT";

    /** 消息被接收并解析成功。设备或者网关收到该确认代码后，应该根据原先定义的上传时间间隔发送下一条信息*/
    public static String ACK = "ACK";

    /** 超时错误码*/
    public static int STATUS_TIME_OUT = 408;
    public static int STATUS_CANCEL = 401;
    /** 服务器返回 未知错误*/
    public static int STATUS_UNKNOWN = 301;

}
