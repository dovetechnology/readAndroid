package com.dove.readandroid

import android.content.Context
import android.widget.ImageView
import com.youth.banner.loader.ImageLoader

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/5/31 9:42
 * ===============================
 */
class BannerImageloadr : ImageLoader() {
    override fun displayImage(context: Context, path: Any, imageView: ImageView) {
        com.appbaselib.common.ImageLoader.load(context, path.toString(), imageView)
    }
}