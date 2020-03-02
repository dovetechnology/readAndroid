package com.dove.readandroid.ui.shucheng

import android.content.Intent
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import anet.channel.strategy.dispatch.IAmdcSign
import com.appbaselib.common.ImageLoader
import com.appbaselib.view.RatioImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dove.readandroid.R
import com.dove.readandroid.ui.model.Fenlei

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/5/7 10:03
 * ===============================
 */
class FenLeiAdapter(layout: Int, data: MutableList<Fenlei>?) :
    BaseQuickAdapter<Fenlei, BaseViewHolder>(layout, data) {

   public var width=0;

    override fun convert(helper: BaseViewHolder, item: Fenlei?) {
        var textviewWidth = helper.getView<TextView>(R.id.tvName)

        helper.setText(R.id.tvName, item?.name)
//        if (!item?.url.isNullOrEmpty())
//            ImageLoader.load(context, item?.url ?: "", helper.getView(R.id.iv_shu))
        var imageview = helper.getView<ImageView>(R.id.iv_shu)
        var width = width
        var param = imageview.layoutParams
        param.width = width
        imageview.layoutParams = param


        item?.let {
            when (helper.layoutPosition) {
                0 -> {

                    imageview
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dushi))
                }
                1 -> {
                    imageview
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.lingyi))
                }
                2 -> {
                    imageview
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.xuanhuan))
                }
                3 -> {
                    imageview
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.jingji))
                }
                4 -> {
                    imageview
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.xianxia))
                }
                5 -> {
                    imageview
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.lishi))
                }
                6 -> {
                    imageview
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.nvsheng))
                }
                7 -> {
                    imageview
                        .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.erciyuan))
                }
                else -> {

                }

            }
        }
    }

}