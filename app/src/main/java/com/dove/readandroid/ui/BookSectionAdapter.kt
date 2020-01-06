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

class BookSectionAdapter(layoutResId: Int, data: MutableList<BookSectionItem>) :
    BaseRecyclerViewAdapter<BookSectionItem>(layoutResId, data) {
    private var textColor = -1


    protected override fun convert(helper: BaseViewHolder, item: BookSectionItem?) {
        val textView = helper.getView<TextView>(R.id.tv_section_name)
        textView.text = item?.title
        if (textColor == -1) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
        } else {
            textView.setTextColor(textColor)
        }
    }

    fun setTextColor(color: Int) {
        textColor = color
        notifyDataSetChanged()
    }
}
