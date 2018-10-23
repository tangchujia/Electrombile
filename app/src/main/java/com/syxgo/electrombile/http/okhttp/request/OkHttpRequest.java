package com.syxgo.electrombile.http.okhttp.request;




import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @Author: Huangweicai
 * @date 2016-08-18 20:55
 * @Description:(这里用一句话描述这个类的作用)
 */
public abstract class OkHttpRequest {
    protected String url;
    protected Object tag;
    protected Map<String, Object> params;
    protected Map<String, String> headers;
    protected Object jsonObject;
    protected int id;

    protected Request.Builder builder = new Request.Builder();

    protected OkHttpRequest(String url, Object tag,
                            Map<String, Object> params, Map<String, String> headers,int id)
    {
        this(url,tag,null,params,headers,id);
    }

    protected OkHttpRequest(String url, Object tag,
                            Object jsonObject,Map<String, Object> params, Map<String, String> headers,int id)
    {
        this.url = url;
        this.tag = tag;
        this.jsonObject = jsonObject;
        this.params = params;
        this.headers = headers;
        this.id = id ;

        if (url == null)
        {
            throw new IllegalArgumentException("url is null...");
        }

        initBuilder();
    }



    /**
     * 初始化一些基本参数 url , tag , headers
     */
    private void initBuilder()
    {
        builder.url(url).tag(tag);
        appendHeaders();
    }

    protected abstract RequestBody buildRequestBody();

    protected RequestBody wrapRequestBody(RequestBody requestBody, final NetResponseListener listener)
    {
        return requestBody;
    }

    protected abstract Request buildRequest(RequestBody requestBody);

    public BaseRequestCall build()
    {
        return new BaseRequestCall(this);
    }


    public Request generateRequest(NetResponseListener listener)
    {
        RequestBody requestBody = buildRequestBody();
        RequestBody wrappedRequestBody = wrapRequestBody(requestBody, listener);
        Request request = buildRequest(wrappedRequestBody);
        return request;
    }


    /**
     * 将头信息添加到okHttp builder中
     */
    protected void appendHeaders()
    {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) return;

        for (String key : headers.keySet())
        {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }

    public int getId()
    {
        return id  ;
    }

}
