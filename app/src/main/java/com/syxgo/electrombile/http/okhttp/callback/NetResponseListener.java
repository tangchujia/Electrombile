package com.syxgo.electrombile.http.okhttp.callback;

/**
 * @Author: Huangweicai
 * @date 2016-08-18 17:37
 * @Description: 网络请求监听接口 关联对象{@link NetResponse}
 */
public abstract class NetResponseListener {


    public abstract void onSuccess(NetResponse netResponse);

    public abstract void onFailed(NetResponse netResponse);


    public void onThreadSuccess(NetResponse netResponse) {

    }

    public void onThreadFailed(NetResponse netResponse) {

    }
}
