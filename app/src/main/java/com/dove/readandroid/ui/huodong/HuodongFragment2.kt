package com.dove.readandroid.ui.huodong

import android.view.View
import com.appbaselib.adapter.lightAdapter.LightAdapter
import com.appbaselib.adapter.lightAdapter.LoadMoreAdapter
import com.appbaselib.common.ImageLoader
import com.appbaselib.view.RatioImageView
import com.dove.imuguang.base.BaseRefreshFragment2
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.WebViewActivity
import com.dove.readandroid.ui.model.Huodong

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class HuodongFragment2 : BaseRefreshFragment2<Huodong>() {

    override fun initView() {
        super.initView()
        toggleShowLoading(true)
        requestData()

    }

    override fun initAdapter() {
        mAdapter = LoadMoreAdapter<Huodong>().register(R.layout.item_huodong) {
            //绑定
            var item = mList.get(it.adapterPosition)
            ImageLoader.load(mContext, item.img, it.getView(R.id.iv_bg))
            it.setText(R.id.tvTitle, item.title)
            if (System.currentTimeMillis() > (item.endTime))
                it.setText(R.id.state, "进行中")
            else {
                it.setText(R.id.state, "已结束")

            }
        }
        mAdapter.itemClickListener =
            { lightAdapter: LoadMoreAdapter<Huodong>, view: View, position: Int ->
                WebViewActivity.instance(mList.get(position).path, mContext)
            }
        setLoadMore()
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