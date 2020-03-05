package com.dove.readandroid.ui.me

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.appbaselib.base.BaseMvcActivity
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.App
import com.dove.readandroid.ui.ReadActivity
import com.safframework.ext.click
import kotlinx.android.synthetic.main.activity_read_hsitory.*
import kotlinx.android.synthetic.main.title_bar.*
import java.util.*

class ReadHsitoryActivity : BaseMvcActivity() {

    override fun initView(mSavedInstanceState: Bundle?) {

        titleBar.setTitle("阅读历史")

        recyclerview.layoutManager = LinearLayoutManager(mContext)
        var mBooks = App.instance.db.getBookDao().allBook?.apply {
            Collections.reverse(this)
        }
        var mAdapter = HistoryBookAdapter(R.layout.item_lishishu, mBooks)
        recyclerview.adapter = mAdapter
        mAdapter.setOnItemClickListener { adapter, view, position ->


            start(ReadActivity::class.java, Bundle().apply {
                putSerializable(
                    "data",
                    App.instance.db.getBookDao().find(mBooks?.get(position)?.name)
                )
            })
        }
        titleBar.setRightTitle("清空", this).click {
            App.instance.db.getBookDao().deleteAll()
            mBooks?.clear()
            mAdapter.notifyDataSetChanged()
        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_read_hsitory
    }

}
