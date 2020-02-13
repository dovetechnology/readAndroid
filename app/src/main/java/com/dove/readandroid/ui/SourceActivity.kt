package com.dove.readandroid.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.ext.toast
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dove.readandroid.R
import com.dove.readandroid.network.get
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.model.Source
import kotlinx.android.synthetic.main.activity_source.*
import kotlinx.android.synthetic.main.title_bar.*

class SourceActivity : BaseMvcActivity() {
    lateinit var adapter: NameAdapter
    lateinit var mbook:Book
    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_source
    }

    override fun initView(mSavedInstanceState: Bundle?) {
        titleBar.setTitle("选择来源")
        mbook= intent.getSerializableExtra("data") as Book
        recyclerview.layoutManager = LinearLayoutManager(mContext)
        recyclerview.adapter = NameAdapter(R.layout.item_paihang_title, mutableListOf()).apply {
            adapter = this
        }
        http().mApiService.source()
            .get3  (isShowDialog = true){
                adapter.addData(it!!)
            }
        adapter.setOnItemClickListener { a, view, position ->
                http().mApiService.sourceChange(mbook.name,mbook.author,mbook.title,adapter.data.get(position).id)
                    .get (isShowDialog = true){
                        toast("切换成功")
                        finish()
                    }
        }
    }

    class NameAdapter(layout: Int, data: MutableList<Source>?) :
        BaseQuickAdapter<Source, BaseViewHolder>(layout, data) {
        override fun convert(helper: BaseViewHolder, item: Source?) {

            helper.setText(R.id.tvName, item?.name)

        }

    }
}
