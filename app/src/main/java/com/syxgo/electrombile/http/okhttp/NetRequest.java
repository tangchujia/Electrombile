package com.syxgo.electrombile.http.okhttp;

import android.util.Log;


import com.syxgo.electrombile.http.okhttp.builder.DeleteBuilder;
import com.syxgo.electrombile.http.okhttp.builder.GetBuilder;
import com.syxgo.electrombile.http.okhttp.builder.PostBuilder;
import com.syxgo.electrombile.http.okhttp.builder.PostHttpsBuilder;
import com.syxgo.electrombile.http.okhttp.builder.PutBuilder;
import com.syxgo.electrombile.http.okhttp.callback.NetResponse;
import com.syxgo.electrombile.http.okhttp.callback.NetResponseListener;
import com.syxgo.electrombile.http.okhttp.request.BaseRequestCall;
import com.syxgo.electrombile.http.okhttp.utils.LogUtil;
import com.syxgo.electrombile.http.okhttp.utils.Platform;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * @Author: Huangweicai
 * @date 2016-08-18 17:26
 * @Description:网络请求管理类
 */
public class NetRequest {




    private static final String LOG_TAG = "NetRequest";
    private Platform mPlatform;

    private volatile static NetRequest mInstance;

    private OkHttpClient mOkHttpClient;

    public NetRequest(OkHttpClient okHttpClient) {
        mPlatform = Platform.get();
        if (okHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        } else {
            mOkHttpClient = okHttpClient;
        }
    }


    public static NetRequest getInstance() {
        return initClient(null);
    }


    public static NetRequest initClient(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            synchronized (NetRequest.class) {
                if (mInstance == null) {
                    mInstance = new NetRequest(okHttpClient);
                }
            }
        }
        return mInstance;
    }

    /**
     * 设置请求类型 该类型为https 后续可能有其他类型
     *
     * @return
     */
    public static PostHttpsBuilder postHttps() {
        return new PostHttpsBuilder();
    }

    /**
     * 设置get请求类型
     * @return
     */
    public static GetBuilder get()
    {
        return new GetBuilder();
    }

    public static PostBuilder post() {
        return new PostBuilder();
    }

    public static PutBuilder put() {
        return new PutBuilder();
    }

    public static DeleteBuilder delete() {
        return new DeleteBuilder();
    }

    /**
     * @param call
     * @param listener
     */
    public void execute(BaseRequestCall call, final NetResponseListener listener) {

        if (call == null) {
            Log.w("TAG", "call is null......");
            return;
        }
        call.getCall().enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                LogUtil.i("请求失败:" + e.getMessage());
                if (listener == null) {
                    return;
                }
                if (listener == null) return;
                int responseCoed = -1;
                final NetResponse response = new NetResponse();
                response.setMsg(e.getMessage());
                response.setCode(responseCoed);
                if (e instanceof SocketTimeoutException) {
                    response.setCode(NetStatus.STATUS_TIME_OUT);
                    response.setMsg("请求超时");
                }

                if (e instanceof ConnectException) {
                    response.setCode(NetStatus.STATUS_TIME_OUT);
                    response.setMsg("连接异常");
                }
                mPlatform.execute(new Runnable() {
                    @Override
                    public void run() {

                        listener.onFailed(response);//失败处理
                    }
                });
                listener.onThreadFailed(response);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseStr = response.body().string();
                LogUtil.i("返回报文:" + responseStr);
                if (listener == null) {
                    return;
                }
                if (responseStr.contains("errorMessage"))
                {//包含错误信息
                    listener.onFailed(new NetResponse(NetStatus.STATUS_UNKNOWN,responseStr));
                    return;
                }
                final NetResponse resultStr = new NetResponse();
                resultStr.setCode(response.code());
                resultStr.setResult(responseStr);
                mPlatform.execute(new Runnable() {
                    @Override
                    public void run() {
                        //成功处理

                        listener.onSuccess(resultStr);
                    }
                });
                listener.onThreadSuccess(resultStr);
            }
        });
    }


    /**
     * 通过tag取消请求
     *
     * @param tag
     */
    public void cancelTag(Object tag) {
        if (mOkHttpClient == null) {
            LogUtil.w("mOkHttpClient is null...");
            return;
        }
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                LogUtil.d("queuedCalls网络请求被取消 TAG:" + tag);
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                LogUtil.d("runningCalls网络请求被取消 TAG:" + tag);
                call.cancel();
            }
        }
    }

    public void cancelAll() {
        if (mOkHttpClient == null) {
            LogUtil.w("mOkHttpClient is null...");
            return;
        }
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            LogUtil.d("queuedCalls网络请求被取消 cancelAll:");
            call.cancel();
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            LogUtil.d("runningCalls网络请求被取消 cancelAll:");
            call.cancel();
        }
    }

    public void setOkHttpClient(OkHttpClient mOkHttpClient) {
        this.mOkHttpClient = mOkHttpClient;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
}
