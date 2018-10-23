package com.syxgo.electrombile.http.okhttp.builder;

import android.net.Uri;


import com.syxgo.electrombile.http.okhttp.request.BaseRequestCall;
import com.syxgo.electrombile.http.okhttp.request.GetRequest;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Huangweicai
 * @date 2016-08-24 13:34
 * @Description:get请求builder
 */
public class GetBuilder extends OkHttpRequestBuilder<GetBuilder>{
    @Override
    public BaseRequestCall build()
    {
        if (params != null)
        {
            url = appendParams(url, params);
        }

        return new GetRequest(url, tag, params, headers,id).build();
    }

    protected String appendParams(String url, Map<String, Object> params)
    {
        if (url == null || params == null || params.isEmpty())
        {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext())
        {
            String key = iterator.next();
            builder.appendQueryParameter(key, params.get(key).toString());
        }
        return builder.build().toString();
    }


    @Override
    public GetBuilder params(Map<String, Object> params)
    {
        this.params = params;
        return this;
    }

    public GetBuilder addParams(String key, Object val)
    {
        if (this.params == null)
        {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }
}
