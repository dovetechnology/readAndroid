package com.dove.readandroid.ui;

import android.text.TextUtils;
import android.util.Log;

import com.appbaselib.utils.PreferenceUtils;
import com.dove.readandroid.BuildConfig;
import com.dove.readandroid.ui.common.CommonParamsInterceptor;
import com.dove.readandroid.ui.common.Constants;
import com.dove.readandroid.ui.shujia.DownLoadApiService;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ===============================
 * 描    述：提供Http相关对象
 * ===============================
 */
public class RetrofitHelper {

    private static RetrofitHelper mRetrofitHelper;
    private Retrofit mRetrofit;
    public ApiService mApiService;

    private RetrofitHelper() {


        //创建OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new CommonParamsInterceptor());//添加参数拦截器
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.e("HttpReponse:", message);
                }
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        //设置超时
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.callTimeout(10, TimeUnit.SECONDS);
        File httpCacheDirectory = new File(App.instance.getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        builder.cache(cache);
        OkHttpClient okHttpClient = builder.build();

        String url = PreferenceUtils.getPrefString(App.Companion.getInstance(), Constants.URL, "");
        if (TextUtils.isEmpty(url)) {
            url = BuildConfig.BASE_URL; //用默认的
        }
        mRetrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mApiService = mRetrofit.create(ApiService.class);

    }

    public static synchronized RetrofitHelper getInstance() {
        if (mRetrofitHelper == null) {
            mRetrofitHelper = new RetrofitHelper();
        }
        return mRetrofitHelper;
    }

    private Retrofit mRetrofitDownload;
    private DownLoadApiService mService;

    public DownLoadApiService getDownLoadService() {
        if (mService==null)
        {
            mRetrofitDownload = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(new OkHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mService = mRetrofitDownload.create(DownLoadApiService.class);
        }
        return  mService;
    }

}
