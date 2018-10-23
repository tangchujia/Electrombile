package com.syxgo.electrombile.http.okhttp.builder;




import com.syxgo.electrombile.http.okhttp.request.BaseRequestCall;
import com.syxgo.electrombile.http.okhttp.request.PostHttpsRequest;

import java.io.InputStream;

/**
 * @Author: Huangweicai
 * @date 2016-08-18 17:21
 * @Description:Https post 请求
 */
public class PostHttpsBuilder extends OkHttpRequestBuilder<PostHttpsBuilder> {

    public PostHttpsBuilder() {
//        initHttpsClient();
    }

    /**
     * 初始化https client
     *
     * @param certificates
     * @param bksFile
     * @param password
     */
    public void initHttpsClient(InputStream[] certificates, InputStream bksFile, String password) {
//        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(certificates, bksFile, password);
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
//                .build();
//        NetRequest.initClient(okHttpClient);
    }


    @Override

    public BaseRequestCall build() {
        PostHttpsRequest request = new PostHttpsRequest(url, tag, params, headers, id);
        return request.build();
    }
}
