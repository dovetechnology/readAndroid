package com.dove.rea

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.appbaselib.base.BaseMvcFragment
import com.appbaselib.common.load
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.BookDetailActivity
import com.dove.readandroid.ui.shucheng.HomeBookAdapter
import com.safframework.ext.click
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
        adapter.setOnItemClickListener { a, view, position ->
            start(BookDetailActivity::class.java, Bundle().apply {
                putSerializable("data", adapter.data.get(position))
            })
        }
        rv_xinshu.layoutManager = GridLayoutManager(mContext, 3)
        rv_xinshu.adapter = HomeBookAdapter(R.layout.item_shu, arrayListOf()).apply {
            adapterx = this
        }
        adapterx.setOnItemClickListener { a, view, position ->
            start(BookDetailActivity::class.java, Bundle().apply {
                putSerializable("data", adapterx.data.get(position))
            })
        }

        iv_close.click {
            cd_ad.visibility = View.GONE
        }
        iv_close_two.click {
            cd_ad_two.visibility = View.GONE
        }

        toggleShowLoading(true)
        getData()
        //广告1
        http().mApiService.ad("3")
            .get3 {
                if (it != null && it.list != null && it.list.size > 0) {
                    iv_ad_one.load(it?.list?.get(0)?.imgUrl)
                }
            }
        //广告2
        http().mApiService.ad("4")
            .get3 {
                if (it != null && it.list != null && it.list.size > 0) {
                    iv_ad_two.load(it?.list?.get(0)?.imgUrl)
                }
            }
    }

    fun getData() {
        http().mApiService.home()
            .get3(next = {
                toggleShowLoading(false)
                it?.hot?.let { it1 -> adapter.addData(it1) }
                it?.newin?.let { it1 -> adapterx.addData(it1) }
            }, err = {
                toggleNetworkError(true, {
                    toggleShowLoading(true)
                    getData()
                })
            })

    }

}