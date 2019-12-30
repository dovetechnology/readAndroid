package com.dove.readandroid.ui

import androidx.recyclerview.widget.GridLayoutManager
import com.dove.imuguang.base.BaseRefreshFragment
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.shucheng.HomeBookAdapter
import kotlinx.android.synthetic.main.fragment_recyclerview.*

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/27 17:52
 * ===============================
 */
class BookFragment : BaseRefreshFragment<Book>() {
    override fun initAdapter() {
        mAdapter=HomeBookAdapter(R.layout.item_shu,mList)
        recyclerview.layoutManager=GridLayoutManager(mContext,3)
    }

    override fun requestData() {
        http().mApiService.search(ms)
            .get3 {

            }
    }
 var ms=""
    fun search(string: String) {
        ms=string
            requestData()
    }
}