package com.dove.readandroid.ui;

import android.text.TextUtils;
import android.util.Log;

import com.appbaselib.utils.PreferenceUtils;
import com.dove.readandroid.BuildConfig;
import com.dove.readandroid.ui.common.CommonParamsInterceptor;
import com.dove.readandroid.ui.common.Constants;
import com.dove.readandroid.ui.common.Retry;

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
       // builder.addInterceptor(new Retry(3));//重试次数  不知道为什么没效果
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
        builder.connectTimeout(5, TimeUnit.SECONDS);
        builder.readTimeout(5, TimeUnit.SECONDS);
        builder.writeTimeout(5, TimeUnit.SECONDS);
        builder.callTimeout(5, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
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

    private Retrofit.Builder provideRetrofitBuilder() {
        return new Retrofit.Builder();
    }

    private OkHttpClient.Builder provideOkHttpClientBuilder() {
        return new OkHttpClient.Builder();
    }

    public OkHttpClient provideOkHttpClient(OkHttpClient.Builder builder) {
        return OkHttpClientConfig(builder);
    }

    public Retrofit provideRetrofit(Retrofit.Builder builder, OkHttpClient okHttpClient) {
        String url = PreferenceUtils.getPrefString(App.Companion.getInstance(), Constants.URL, "");
        if (TextUtils.isEmpty(url)) {
            url = BuildConfig.BASE_URL; //用默认的
        }
        mRetrofit = builder
                .baseUrl(url)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return mRetrofit;
    }

    public OkHttpClient OkHttpClientConfig(OkHttpClient.Builder builder) {
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
        //错误重连
        builder.retryOnConnectionFailure(true);
        File httpCacheDirectory = new File(App.instance.getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        builder.cache(cache);
        return builder.build();
    }
}
