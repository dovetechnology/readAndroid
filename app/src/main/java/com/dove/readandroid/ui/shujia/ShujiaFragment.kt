package com.dove.readandroid.ui.shujia

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.appbaselib.base.BaseMvcFragment
import com.dove.imuguang.base.BaseRefreshFragment
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.shucheng.HomeBookAdapter
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
    }

    override fun initView() {
        super.initView()
       // toggleShowLoading(true)
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