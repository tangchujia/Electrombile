package com.syxgo.electrombile.http.okhttp.request;


import com.syxgo.electrombile.http.okhttp.NetRequest;
import com.syxgo.electrombile.http.okhttp.builder.OkHttpRequestBuilder;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Author: Huangweicai
 * @date 2016-08-18 18:01
 * @Description:基础call类 在 {@link OkHttpRequestBuilder} build的时候返回该对象
 * 在该类创建call对象后交由 {@link NetRequest}去excute执行
 */
public class BaseRequestCall {

    private OkHttpRequest okHttpRequest;
    private Request request;
    private Call call;

    private final long DEFAULT_MILLISECONDS = 2000L;
    private long readTimeOut = DEFAULT_MILLISECONDS;
    private long writeTimeOut = DEFAULT_MILLISECONDS;
    private long connTimeOut = DEFAULT_MILLISECONDS;

    private OkHttpClient clone;

    public BaseRequestCall(OkHttpRequest request) {
        this.okHttpRequest = request;
    }

    public BaseRequestCall readTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return this;
    }

    public BaseRequestCall writeTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    public BaseRequestCall connTimeOut(long connTimeOut) {
        this.connTimeOut = connTimeOut;
        return this;
    }

    public Call buildCall(NetResponseListener listener) {

        request = generateRequest(listener);

        if (readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0) {
            readTimeOut = readTimeOut > 0 ? readTimeOut : DEFAULT_MILLISECONDS;
            writeTimeOut = writeTimeOut > 0 ? writeTimeOut : DEFAULT_MILLISECONDS;
            connTimeOut = connTimeOut > 0 ? connTimeOut : DEFAULT_MILLISECONDS;

            clone = NetRequest.getInstance().getOkHttpClient().newBuilder()
                    .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                    .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
                    .build();

            call = clone.newCall(request);
        } else {
            call = NetRequest.getInstance().getOkHttpClient().newCall(request);
        }
        return call;
    }

    private Request generateRequest(NetResponseListener listener) {
        return okHttpRequest.generateRequest(listener);
    }

    public void execute(NetResponseListener listener) {
        buildCall(listener);


        NetRequest.getInstance().execute(this, listener);
    }

    public Call getCall() {
        return call;
    }

    public Request getRequest() {
        return request;
    }

    public OkHttpRequest getOkHttpRequest() {
        return okHttpRequest;
    }

    public Response execute() {
        buildCall(null);
        try {
            return call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

}
