package com.syxgo.electrombile.http.okhttp.request;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by tangchujia on 2017/3/24.
 */

public class PutRequest extends OkHttpRequest {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String proxyId = "";


    public PutRequest(String url, Object tag, Object jsonObject, Map<String, Object> params, Map<String, String> headers, int id) {
        super(url, tag, jsonObject ,params, headers, id);
    }


    @Override
    protected RequestBody buildRequestBody() {

        String jsonStr = JSONObject.toJSONString(jsonObject);



        return RequestBody.create(JSON, jsonStr);
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.put(requestBody).build();
    }


    /**
     * 将Params转为json字符串
     * @return
     */
    private String getJson() {
        if (params != null) {
            JSONObject jsonObject = new JSONObject();
            for (String key : params.keySet()) {
                jsonObject.put(key, params.get(key));
            }
            return jsonObject.toString();
        }
        return "";
    }

}
