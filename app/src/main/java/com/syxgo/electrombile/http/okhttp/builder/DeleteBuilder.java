package com.syxgo.electrombile.http.okhttp.builder;


import com.syxgo.electrombile.http.okhttp.request.BaseRequestCall;
import com.syxgo.electrombile.http.okhttp.request.DeleteRequest;

/**
 * Created by tangchujia on 2017/2/22.
 */

public class DeleteBuilder extends OkHttpRequestBuilder<DeleteBuilder> {

    private Object jsonObject;

    public DeleteBuilder() {

    }


    public DeleteBuilder jsonObject(Object jsonObject) {
        this.jsonObject = jsonObject;
        return this;
    }


    @Override

    public BaseRequestCall build() {
        DeleteRequest request = new DeleteRequest(url, tag, jsonObject,params, headers, id);
        return request.build();
    }
}