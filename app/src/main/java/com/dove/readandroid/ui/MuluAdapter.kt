package com.dove.readandroid.ui

import android.widget.TextView

import androidx.core.content.ContextCompat

import com.appbaselib.base.BaseRecyclerViewAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dove.readandroid.R
import com.dove.readandroid.ui.model.BookSectionItem

/**
 * author : zchu
 * date   : 2017/11/3
 * desc   :
 */

class MuluAdapter(layoutResId: Int, data: MutableList<String>) :
    BaseRecyclerViewAdapter<String>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: String?) {
        val textView = helper.getView<TextView>(R.id.tv_name)
        textView.text = item

    }

}
