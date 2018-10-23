package com.syxgo.electrombile.http.okhttp.builder;




import com.syxgo.electrombile.http.okhttp.request.BaseRequestCall;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @Author: Huangweicai
 * @date 2016-08-18 17:03
 * @Description:Builder基础类
 *                     子类可实现不同类型的请求
 *
 *                     通过 {@link #url(String)}等方法设置参数完后
 *                     调用 {@link #build()}后返回 {@link BaseRequestCall}类
 */

public abstract class OkHttpRequestBuilder<T extends OkHttpRequestBuilder> {

    protected String url;
    protected Object tag;
    protected Map<String, String> headers;
    protected Map<String, Object> params;
    protected int id;

    public T id(int id)
    {
        this.id = id;
        return (T) this;
    }

    public T url(String url)
    {
        this.url = url;
        return (T) this;
    }


    public T tag(Object tag)
    {
        this.tag = tag;
        return (T) this;
    }

    public T headers(Map<String, String> headers)
    {
        this.headers = headers;
        return (T) this;
    }

    public T params(Map<String, Object> params) {
        this.params = params;
        return (T) this;
    }

    public T addHeader(String key, String val)
    {
        if (this.headers == null)
        {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return (T) this;
    }

    public T addParam(String key,Object val) {
        if ((this.params == null)) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return (T) this;
    }

    public abstract BaseRequestCall build();
}
