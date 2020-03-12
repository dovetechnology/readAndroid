package com.dove.readandroid.ui.shucheng

import android.content.Intent
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
 * 创建日期：2019/5/7 10:03
 * ===============================
 */
class FenleiContentAdapter(layout: Int, data: MutableList<Book>) :
    BaseRecyclerViewAdapter<Book>(layout, data) {
    override fun convert(helper: BaseViewHolder, item: Book?) {
        item?.let {
            //  helper.setText(R.id.tvName, item.name)
            helper.setText(R.id.tv_name, item.name)
            helper.setText(R.id.tv_type, item.category.trim())
            helper.setText(R.id.tv_author, item.author)
            helper.setText(R.id.tv_desc, item.description.trim())

            ImageLoader.load(context, item?.coverImage ?: "", helper.getView(R.id.iv_shu), 4f)

        }
//        when(helper.adapterPosition)
//        {
//            0->{
//                helper.setVisible(R.id.iv_tag,true)
//                helper.setImageDrawable(R.id.iv_tag,ContextCompat.getDrawable(context,R.drawable.icon_one))
//            }
//            1->{
//                helper.setVisible(R.id.iv_tag,true)
//                helper.setImageDrawable(R.id.iv_tag,ContextCompat.getDrawable(context,R.drawable.icon_two))
//            }
//            2->{
//                helper.setVisible(R.id.iv_tag,true)
//                helper.setImageDrawable(R.id.iv_tag,ContextCompat.getDrawable(context,R.drawable.icon_three))
//            }
//            else->{
//                helper.setVisible(R.id.iv_tag,false)
//
//            }
//        }


    }

}