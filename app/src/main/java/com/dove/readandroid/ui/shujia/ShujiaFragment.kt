package com.dove.readandroid.ui.shujia

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.appbaselib.base.BaseMvcFragment
import com.dove.imuguang.base.BaseRefreshFragment
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.BookDetailActivity
import com.dove.readandroid.ui.ReadActivity
import com.dove.readandroid.ui.SearchActivity
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.shucheng.HomeBookAdapter
import com.safframework.ext.click
import kotlinx.android.synthetic.main.fragment_shujia.*

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class ShujiaFragment : BaseRefreshFragment<Book>() {
    override fun initAdapter() {
        mAdapter = HomeBookAdapter(R.layout.item_shu, mList)
        recyclerview.layoutManager = GridLayoutManager(mContext, 3)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            var book = mList.get(position)
            http().mApiService.openName(book.name, book.author, "")
                .get3(isShowDialog = true) {
                    start(ReadActivity::class.java, Bundle().apply {
                        putSerializable("data", it?.data)
                    })
                }

        }
    }

    override fun initView() {
        super.initView()
        // toggleShowLoading(true)
        etSearch.click {
            start(SearchActivity::class.java)
        }
        requestData()
    }

    override fun getLoadingTargetView(): View? {
        return swipe
    }

    override fun requestData() {
        http().mApiService.shujiaList()
            .get3 {
                loadComplete(it)
            }
    }


    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_shujia
    }

}