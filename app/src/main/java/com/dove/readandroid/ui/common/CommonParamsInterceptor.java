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
//        if (TextUtils.equals(oldRequest.method(), "GET") || TextUtils.equals(oldRequest.method(), "get")) {
//            //get请求参数未处理
//            return chain.proceed(oldRequest);
//        }
        //获取url对象
        HttpUrl httpUrl = oldRequest.url();
//        //创建新的请求HttpUrl
//        HttpUrl.Builder newBuilder = httpUrl.newBuilder()
//                .scheme(httpUrl.scheme())
//                .host(httpUrl.host());
        // 新的请求
        Request newRequest = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body())
                .url(httpUrl)
                .tag(oldRequest.tag())
                .cacheControl(oldRequest.cacheControl())
                .addHeader("Connection", "close")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("token", UserShell.getInstance().getToken())
                .addHeader("user_agent", CommonParamUtil.userAgent())//设备信息
                .addHeader("channel", "android")////大渠道
                .addHeader("channel_package", "")//小渠道
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
