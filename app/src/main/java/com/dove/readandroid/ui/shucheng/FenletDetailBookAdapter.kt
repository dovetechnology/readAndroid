package com.dove.readandroid.ui.shucheng

import com.appbaselib.base.BaseRecyclerViewAdapter
import com.appbaselib.common.ImageLoader
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dove.readandroid.R
import com.dove.readandroid.ui.model.Book

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/3/4 10:14
 * ===============================
 * 带加载更多
 */
class FenletDetailBookAdapter(layout: Int, data: MutableList<Book>) :
    BaseRecyclerViewAdapter<Book>(layout, data) {
    override fun convert(helper: BaseViewHolder, item: Book?) {

        item?.let {
            helper.setText(R.id.tv_name, item.name)

            if (!item.coverImage.isNullOrEmpty())
                ImageLoader.load(context, item.coverImage, helper.getView(R.id.iv_shu))

        }
    }

}
