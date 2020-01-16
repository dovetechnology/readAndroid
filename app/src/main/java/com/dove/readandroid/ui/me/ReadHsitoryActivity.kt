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
import kotlinx.android.synthetic.main.activity_read_hsitory.*

class ReadHsitoryActivity : BaseMvcActivity() {

    override fun initView(mSavedInstanceState: Bundle?) {

        recyclerview.layoutManager = LinearLayoutManager(mContext)
        var mBooks = App.instance.db.getBookDao().allBook
        var mAdapter = HistoryBookAdapter(R.layout.item_lishishu, mBooks)
        recyclerview.adapter = mAdapter
        mAdapter.setOnItemClickListener { adapter, view, position ->
            http().mApiService.openName(mBooks.get(position).name, mBooks.get(position).author, "")

                .get3(isShowDialog = true) {
                    start(ReadActivity::class.java, Bundle().apply {
                        putSerializable("data", it?.data)
                    })
                }
        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_read_hsitory
    }

}
