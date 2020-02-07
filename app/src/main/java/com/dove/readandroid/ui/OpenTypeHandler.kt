package com.dove.readandroid.ui

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/11/18 15:22
 * ===============================
 */
class OpenTypeHandler(var type: String, var context: Context, var url: String) {

    fun handle() {
        if ("4".equals(type)) {
            return
        }
        if ("3".equals(type)) {
            WebViewActivity.instance(url, context)
        } else {
            var intent = Intent();
            intent.setData(Uri.parse(url));//Url 就是你要打开的网址
            intent.setAction(Intent.ACTION_VIEW);
            context.startActivity(intent); //启动浏览器
        }
    }

}
