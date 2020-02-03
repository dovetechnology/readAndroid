package com.dove.readandroid.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.dove.readandroid.ui.common.CommonParamsInterceptor;

public class JavaScriptInterface {

    public Activity mActivity;

    public JavaScriptInterface(Activity activity) {
        mActivity = activity;
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public void onBackPressed() {
        mActivity.onBackPressed();
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public void closePage() {
        mActivity.finish();
    }

//    @SuppressLint("JavascriptInterface")
//    @JavascriptInterface
//    public String getToken() {
//        return UserShell.getInstance().getToken();
//    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public String getHead() {
        return CommonParamsInterceptor.getRequestHead();
    }

}
