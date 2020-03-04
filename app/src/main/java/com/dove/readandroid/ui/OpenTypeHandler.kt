package com.dove.readandroid.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.model.AdData

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/11/18 15:22
 * ===============================
 */
class OpenTypeHandler(var data: AdData, var context: Context) {

    fun handle() {
        http().mApiService.maidian(data.id).get3 {

        }
        if ("4".equals(data.openType)) {
            return
        }
        if ("3".equals(data.openType)) {
            WebViewActivity.instance(data.forwardUrl, context)
        } else {
            var intent = Intent();
            intent.setData(Uri.parse(data.forwardUrl));//Url 就是你要打开的网址
            intent.setAction(Intent.ACTION_VIEW);
            context.startActivity(intent); //启动浏览器
        }
    }

}
