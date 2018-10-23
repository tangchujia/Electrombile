package com.syxgo.electrombile.http.okhttp.callback;

import com.syxgo.electrombile.http.okhttp.NetStatus;

import java.io.Serializable;

/**
 * @Author: Huangweicai
 * @date 2016-08-18 17:40
 * @Description:网络接口返回给UI  {@link NetResponseListener}
 *              错误码详见 {@link NetStatus}
 */
public class NetResponse implements Serializable{

    private int code;
    private String msg = "";
    private Object result;

    public NetResponse() {

    }

    public NetResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public NetResponse(int code, String msg,Object result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
