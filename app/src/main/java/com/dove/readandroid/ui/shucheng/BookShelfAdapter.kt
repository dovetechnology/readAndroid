package com.dove.readandroid.ui.shucheng

import android.content.Intent
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.appbaselib.common.ImageLoader
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dove.readandroid.R
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.model.BookShelf

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/5/7 10:03
 * ===============================
 */
class BookShelfAdapter(layout: Int, data: MutableList<BookShelf>?) :
    BaseQuickAdapter<BookShelf, BaseViewHolder>(layout, data) {
    override fun convert(helper: BaseViewHolder, item: BookShelf?) {

        item?.let {
            helper.setText(R.id.tv_name, item.name)

            if (!item.img.isNullOrEmpty())
                ImageLoader.load(context, item.img, helper.getView(R.id.iv_shu),4f)

        }
    }

}