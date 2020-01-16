package com.dove.readandroid.ui.common;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ===============================
 * 描    述：添加公共参数拦截器
 * ===============================
 */
public class CommonParamsInterceptor implements Interceptor {

    /**
     * 添加公共参数拦截器
     */
    public CommonParamsInterceptor() {
    }

    @Override
    public synchronized Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();

        Request newRequest = oldRequest.newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("token", UserShell.getInstance().getToken())
                .addHeader("user_agent", CommonParamUtil.userAgent())//设备信息
                .addHeader("channel", "android")////大渠道
                .addHeader("channel_package", "")//小渠道
               // .addHeader("Connection","keep-alive")
                .addHeader("Connection","close")
                .build();
        return chain.proceed(newRequest);
    }

    public static String getRequestHead() {
        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("Connection", "close");
//        hashMap.put("Accept", "application/json");
//        hashMap.put("Content-Type", "application/json; charset=utf-8");
        hashMap.put("token", UserShell.getInstance().getToken());
        hashMap.put("user_agent", CommonParamUtil.userAgent());//设备信息
        return new Gson().toJson(hashMap);
    }

}
