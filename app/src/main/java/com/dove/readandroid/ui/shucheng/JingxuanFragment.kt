package com.dove.rea

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.appbaselib.base.BaseMvcFragment
import com.appbaselib.common.load
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.shucheng.HomeBookAdapter
import kotlinx.android.synthetic.main.fragment_jingxuan.*

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class JingxuanFragment : BaseMvcFragment() {

    lateinit var adapter: HomeBookAdapter
    lateinit var adapterx: HomeBookAdapter
    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_jingxuan
    }

    override fun getLoadingTargetView(): View {
        return swipe
    }

    override fun initView() {
        rv_jingxuan.layoutManager = GridLayoutManager(mContext, 3)
        rv_jingxuan.adapter = HomeBookAdapter(R.layout.item_shu, arrayListOf()).apply {
            adapter = this
        }

        rv_xinshu.layoutManager = GridLayoutManager(mContext, 3)
        rv_xinshu.adapter = HomeBookAdapter(R.layout.item_shu, arrayListOf()).apply {
            adapterx = this
        }
        toggleShowLoading(true)
        getData()
        //广告
        http().mApiService.ad("2")
            .get3 {
                if (it != null && it.list != null && it.list.size > 0) {
                    iv_ad_one.load(it?.list?.get(0)?.imgUrl)
                }
            }
    }

    fun getData() {
        http().mApiService.home()
            .get3 {
                toggleShowLoading(false)
                it?.hot?.let { it1 -> adapter.addData(it1) }
                it?.newin?.let { it1 -> adapterx.addData(it1) }
            }
    }

}