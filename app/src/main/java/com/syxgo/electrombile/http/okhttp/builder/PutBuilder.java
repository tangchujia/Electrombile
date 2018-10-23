package com.syxgo.electrombile.http.okhttp.builder;


import com.syxgo.electrombile.http.okhttp.request.BaseRequestCall;
import com.syxgo.electrombile.http.okhttp.request.PutRequest;

/**
 * Created by tangchujia on 17/1/24.
 */
public class PutBuilder extends OkHttpRequestBuilder<PutBuilder> {

    private Object jsonObject;

    public PutBuilder() {
    }


    public PutBuilder jsonObject(Object jsonObject) {
        this.jsonObject = jsonObject;
        return this;
    }


    @Override

    public BaseRequestCall build() {
        PutRequest request = new PutRequest(url, tag, jsonObject,params, headers, id);
        return request.build();
    }
}