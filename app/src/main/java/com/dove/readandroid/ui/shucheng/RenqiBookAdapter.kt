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

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/5/7 10:03
 * ===============================
 */
class RenqiBookAdapter(layout: Int, data: MutableList<Book>?) :
    BaseQuickAdapter<Book, BaseViewHolder>(layout, data) {
    override fun convert(helper: BaseViewHolder, item: Book?) {

        item?.let {
            helper.setText(R.id.tv_name, item.name)
            helper.setText(R.id.tv_type, item.category)
            helper.setText(R.id.tv_number,helper.adapterPosition.toString())
            if (!item.coverImage.isNullOrEmpty())
                ImageLoader.load(context, item.coverImage, helper.getView(R.id.iv_shu),4f)

        }
        if (helper.adapterPosition<3)
        {
            helper.setTextColor(R.id.tv_number,Color.parseColor("#CDAD85"))
        }
        else{
            helper.setTextColor(R.id.tv_number,Color.parseColor("#252525"))

        }
    }

}