package com.dove.readandroid.ui.shucheng

import android.content.Intent
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.appbaselib.base.BaseRecyclerViewAdapter
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
class PaihangTitleAdapter(layout: Int, data: MutableList<Fenlei>) : BaseRecyclerViewAdapter<Fenlei>(layout,data) {
    override fun convert(helper: BaseViewHolder, item: Fenlei?) {
        
       helper.setText(R.id.tvName, item?.name)

        if (mSinglePosition==helper.layoutPosition)
        {
            helper.setTextColor(R.id.tvName,ContextCompat.getColor(context,R.color.colorAccent))
        }
        else{
            helper.setTextColor(R.id.tvName,Color.parseColor("#3B3B3B"))

        }
    }

}