package com.dove.readandroid.ui.common;

import android.os.UserManager;

import com.appbaselib.utils.PackageUtil;
import com.appbaselib.utils.PreferenceUtils;
import com.appbaselib.utils.SystemUtils;
import com.dove.readandroid.ui.App;
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

        Request original = chain.request();
        if (UserShell.getInstance().isLogin()) {
            Request request = original.newBuilder()
                    .header("token", UserShell.getInstance().getToken())
                    .header("user_agent", CommonParamUtil.userAgent())//设备信息
                    .header("channel", "android")////大渠道
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);

        } else {
            return chain.proceed(original);
        }

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
