package com.syxgo.electrombile.http.okhttp.builder;


import com.syxgo.electrombile.http.okhttp.request.BaseRequestCall;
import com.syxgo.electrombile.http.okhttp.request.PostRequest;

/**
 * @Author: Huangweicai
 * @date 2016-08-18 17:21
 * @Description:post 请求
 */
public class PostBuilder extends OkHttpRequestBuilder<PostBuilder> {

    private Object jsonObject;

    public PostBuilder() {
    }


    public PostBuilder jsonObject(Object jsonObject) {
        this.jsonObject = jsonObject;
        return this;
    }


    @Override

    public BaseRequestCall build() {
        PostRequest request = new PostRequest(url, tag, jsonObject,params, headers, id);
        return request.build();
    }
}
