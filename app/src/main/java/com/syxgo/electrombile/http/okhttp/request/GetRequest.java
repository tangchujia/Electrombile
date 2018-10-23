package com.syxgo.electrombile.http.okhttp.request;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @Author: Huangweicai
 * @date 2016-08-24 13:37
 * @Description:创建request
 */
public class GetRequest extends OkHttpRequest{
    public GetRequest(String url, Object tag, Map<String, Object> params, Map<String, String> headers, int id) {
        super(url, tag, params, headers, id);
    }

    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.get().build();
    }
}
