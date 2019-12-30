package com.dove.rea

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.appbaselib.base.BaseMvcFragment
import com.dove.imuguang.base.BaseRefreshFragment
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.ui.model.Fenlei
import com.dove.readandroid.ui.shucheng.FenLeiAdapter
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.shucheng.FenleiDetailActivity
import kotlinx.android.synthetic.main.fragment_recyclerview.*

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class FenleiFragment : BaseRefreshFragment<Fenlei>() {

    override fun initView() {
        super.initView()
        recyclerview.layoutManager = GridLayoutManager(mContext, 2)
        requestData()
    }

    override fun initAdapter() {
        mAdapter = FenLeiAdapter(R.layout.item_fenlei, mList)
        mAdapter.setOnItemClickListener { adapter, view, position ->

            start(FenleiDetailActivity::class.java, Bundle().apply {
                putString("data",mList.get(position).url)
            })
        }
    }

    override fun requestData() {
        http().mApiService.tag()
            .get3(next = {
                loadComplete(it ?: null)
            }, err = {
                loadError(it)
            })
    }


}