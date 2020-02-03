package com.dove.readandroid.ui.huodong

import android.view.View
import com.appbaselib.base.BaseMvcFragment
import com.dove.imuguang.base.BaseRefreshFragment
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.App
import com.dove.readandroid.ui.WebViewActivity
import com.dove.readandroid.ui.model.Huodong
import kotlinx.android.synthetic.main.fragment_shujia.*

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class HuodongFragment : BaseRefreshFragment<Huodong>() {

    override fun initView() {
        super.initView()
        setLoadMoreListener()
        toggleShowLoading(true)
        requestData()

    }

    override fun initAdapter() {
        mAdapter = HuodongAdapter(R.layout.item_huodong, mList)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            WebViewActivity.instance(mList.get(position).path, mContext)
        }
    }

    override fun requestData() {
        http().mApiService.huodong(pageNo, pageSize)
            .get3(next = {
                loadComplete(it?.list ?: null)
            }, err = {
                loadError(it)
            })
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_huodong
    }

}