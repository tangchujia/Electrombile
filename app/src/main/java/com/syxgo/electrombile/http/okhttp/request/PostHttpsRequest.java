package com.syxgo.electrombile.http.okhttp.request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @Author: Huangweicai
 * @date 2016-08-18 20:58
 * @Description:Https请求 该类用于组装body以及定义Request
 */
public class PostHttpsRequest extends OkHttpRequest {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public PostHttpsRequest(String url, Object tag, Map<String, Object> params, Map<String, String> headers, int id) {
        super(url, tag, params, headers, id);
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(JSON, getJson());
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.post(requestBody).build();
    }


    /**
     * 将Params转为json字符串
     * @return
     */
    private String getJson() {
        if (params != null) {
            JSONObject jsonObject = new JSONObject();
            for (String key : params.keySet()) {
                try {
                    jsonObject.put(key, params.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "";
                }
            }
            return jsonObject.toString();
        }
        return "";
    }

}
