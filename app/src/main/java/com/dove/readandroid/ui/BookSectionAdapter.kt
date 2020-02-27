package com.dove.readandroid.ui

import android.graphics.Color
import android.graphics.Typeface
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


    override fun convert(helper: BaseViewHolder, item: BookSectionItem?) {
        val textView = helper.getView<TextView>(R.id.tv_section_name)
        textView.text = item?.title
        if (textColor == -1) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.black))
        } else {
            textView.setTextColor(textColor)
        }

        if (mSinglePosition==helper.layoutPosition)
        {
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
        }
        else{
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        }

        if (!item?.content.isNullOrEmpty()) {
            //已缓存
            helper.setGone(R.id.iv_download, true)
            helper.setVisible(R.id.tv_yihuancun, true)
            helper.setGone(R.id.animation_view, true)

        } else {

            if (item?.isLoading!!) {
                helper.setVisible(R.id.animation_view, true)
                helper.setGone(R.id.iv_download, true)
                helper.setGone(R.id.tv_yihuancun, true)
            } else {
                helper.setGone(R.id.animation_view, true)
                helper.setVisible(R.id.iv_download, true)
                helper.setGone(R.id.tv_yihuancun, true)
            }
        }

        addChildClickViewIds(R.id.iv_download)
    }

    fun setTextColor(color: Int) {
        textColor = color
        notifyDataSetChanged()
    }
}
