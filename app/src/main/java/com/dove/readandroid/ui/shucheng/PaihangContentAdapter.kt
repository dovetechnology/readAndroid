package com.dove.readandroid.ui.shucheng

import android.content.Intent
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dove.readandroid.R
import com.dove.readandroid.ui.model.Fenlei
import com.dove.readandroid.ui.model.Top

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/5/7 10:03
 * ===============================
 */
class PaihangContentAdapter(layout: Int, data: MutableList<Top>?) : BaseQuickAdapter<Top, BaseViewHolder>(layout, data) {
    override fun convert(helper: BaseViewHolder, item: Top?) {
        item?.let {
            helper.setText(R.id.tvName, item.novelName)
            helper.setText(R.id.name, item.novelName)
        }
    }

}