package com.dove.readandroid.ui.shucheng

import android.content.Intent
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.appbaselib.common.ImageLoader
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dove.readandroid.R
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.model.Fenlei
import com.dove.readandroid.ui.model.Top

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/5/7 10:03
 * ===============================
 */
class HomeBookAdapter(layout: Int, data: MutableList<Book>?) : BaseQuickAdapter<Book, BaseViewHolder>(layout, data) {
    override fun convert(helper: BaseViewHolder, item: Book) {
        
       helper.setText(R.id.tv_name, item.name)

        if (!item.coverImage.isNullOrEmpty())
        ImageLoader.load(mContext,item.coverImage,helper.getView(R.id.iv_shu))

    }

}