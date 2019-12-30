package com.dove.readandroid.ui.huodong

import com.appbaselib.base.BaseRecyclerViewAdapter
import com.appbaselib.common.ImageLoader
import com.appbaselib.view.RatioImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dove.readandroid.R
import com.dove.readandroid.ui.model.Huodong

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/7/30 14:27
 * ===============================
 */
class HuodongAdapter(item: Int, mlist: MutableList<Huodong>) : BaseRecyclerViewAdapter<Huodong>(item, mlist) {
    override fun convert(helper: BaseViewHolder, item: Huodong) {
        ImageLoader.load(mContext, item.img, helper.getView<RatioImageView>(R.id.iv_bg))
        helper.setText(R.id.tvTitle, item.title)
        helper.setText(R.id.state,item.stat)
    }


}
