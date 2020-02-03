package com.dove.readandroid.ui

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.webkit.WebView


/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/8/22 17:05
 * ===============================
 */
class JsBridge(var webview: WebView, var mUri: Uri, var context: Context) {

    fun process() {
        if (mUri.scheme!!.startsWith("js")) {
            val path = mUri.path
            if ("/close".equals(path)) {
                (context as Activity).finish()
            } else if ("/timeClose".equals(path)) {
                Handler().postDelayed({ (context as Activity).finish() }, 1000)
            } else if ("/back".equals(path)) {
                if (webview.canGoBack()) {
                    webview.goBack()
                } else
                    (context as Activity).finish()
            }
        } else {
          webview.loadUrl(mUri.toString())

        }
    }

}