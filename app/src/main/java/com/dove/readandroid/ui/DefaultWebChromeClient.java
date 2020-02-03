package com.dove.readandroid.ui;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import com.appbaselib.app.AppManager;
import com.appbaselib.utils.ToastUtils;
import com.just.agentweb.WebChromeClient;

import java.io.File;

public class DefaultWebChromeClient extends WebChromeClient {
    //交互对象
    private ValueCallback<Uri> mFilePathCallback;
    private ValueCallback<Uri[]> mFilePathCallbacks;
    //调用摄像头和相册选择图片
    private SelLocaImgUtil selLocaImg;
    //相机
    private final int SEL_GALLERY = 2;

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Log.d("webview------------>", consoleMessage.message());
        return super.onConsoleMessage(consoleMessage);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
    }

    public void openFileChooser(ValueCallback<Uri> filePathCallback) {
        openSelectImage(filePathCallback);
    }

    public void openFileChooser(ValueCallback filePathCallback, String acceptType) {
        openSelectImage(filePathCallback);
    }

    public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture) {
        openSelectImage(filePathCallback);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, android.webkit.WebChromeClient.FileChooserParams fileChooserParams) {
        openSelectImages(filePathCallback);
        return true;
    }

    private void openSelectImage(ValueCallback<Uri> filePathCallback) {
        mFilePathCallback = filePathCallback;
        openSelectImages();
    }

    private void openSelectImages(ValueCallback<Uri[]> filePathCallback) {
        mFilePathCallbacks = filePathCallback;
        openSelectImages();
    }

    private void openSelectImages() {

        if (selLocaImg == null)
            selLocaImg = new SelLocaImgUtil();

        int i = selLocaImg.startGallery(AppManager.getInstance().getCurrentActivity(), SEL_GALLERY);
        if (i == SelLocaImgUtil.CALLERROR) {
            ToastUtils.showShort(App.instance,"调用相册失败");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_CANCELED && requestCode == SEL_GALLERY){
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            } else if (mFilePathCallbacks != null) {
                mFilePathCallbacks.onReceiveValue(new Uri[]{null});
                mFilePathCallbacks = null;
            }
        }

        if (requestCode != SEL_GALLERY) return;
        if (resultCode != Activity.RESULT_OK) {
            receiveValue();
            return;
        }
        Uri selectedImage = data.getData();
        String path = selLocaImg.getPath(AppManager.getInstance().getCurrentActivity(), selectedImage);
        if (TextUtils.isEmpty(path)) {
            ToastUtils.showShort(App.instance,"获取图片失败");
            receiveValue();
            return;
        }
        //返回数据
        receiveValue(Uri.fromFile(new File(path)));
    }

    private void receiveValue(Uri uri) {
        if (AppManager.getInstance().getCurrentActivity() == null || AppManager.getInstance().getCurrentActivity().isFinishing()) {
            return;
        }
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(uri);
            mFilePathCallback = null;
        } else if (mFilePathCallbacks != null) {
            mFilePathCallbacks.onReceiveValue(new Uri[]{uri});
            mFilePathCallbacks = null;
        }
    }

    private void receiveValue() {
        if (AppManager.getInstance().getCurrentActivity() == null || AppManager.getInstance().getCurrentActivity().isFinishing()) {
            return;
        }
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        } else if (mFilePathCallbacks != null) {
            mFilePathCallbacks.onReceiveValue(null);
            mFilePathCallbacks = null;
        }
    }
}
