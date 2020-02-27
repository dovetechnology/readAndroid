package com.dove.readandroid.network;

/**
 * Description：
 * Created by kang on 2018/3/9.
 */
 
public interface DownloadListener extends SimpleDownloadListener{
    void onStart();

    void onFinish(String localPath);
 
    void onFailure();
}
