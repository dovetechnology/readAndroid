package com.dove.rea

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.appbaselib.base.BaseMvcFragment
import com.appbaselib.common.load
import com.appbaselib.ext.toast
import com.appbaselib.view.RatioImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dove.readandroid.BannerImageloadr
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.BookDetailActivity
import com.dove.readandroid.ui.OpenTypeHandler
import com.dove.readandroid.ui.model.AdData
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

            var viewHolder = adapter.weakRecyclerView.get()?.findViewHolderForLayoutPosition(position) as BaseViewHolder
            var image = viewHolder.getView<RatioImageView>(R.id.iv_shu)

            start(BookDetailActivity::class.java, Bundle().apply {
                putSerializable("data", adapter.data.get(position))
            }, image, "book")
        }
        rv_xinshu.layoutManager = GridLayoutManager(mContext, 3)
        rv_xinshu.adapter = HomeBookAdapter(R.layout.item_shu, arrayListOf()).apply {
            adapterx = this
        }
        adapterx.setOnItemClickListener { a, view, position ->

            var viewHolder = adapterx.weakRecyclerView.get()?.findViewHolderForLayoutPosition(position) as BaseViewHolder
            var image = viewHolder.getView<RatioImageView>(R.id.iv_shu)

            start(BookDetailActivity::class.java, Bundle().apply {
                putSerializable("data", adapterx.data.get(position))
            }, image, "book")
        }
        banner.setImageLoader(BannerImageloadr())

        swipe.isRefreshing = true
        getData()

        swipe.setOnRefreshListener {
            getData()
        }
    }

    lateinit var adDatas: List<AdData>
    fun getData() {

        //广告1
        http().mApiService.ad("3")
            .get3 {
                it?.list?.let {

                    adDatas = it
                    var list = arrayListOf<String>()
                    it.forEach {
                        list.add(it.imgUrl)
                    }
                    banner.setImages(list)
                    banner.setOnBannerListener {
                        OpenTypeHandler(
                            adDatas?.get(it),
                            mContext
                        ).handle()
                    }
                    banner.start()

                }
            }
        //广告2
//        http().mApiService.ad("4")
//            .get3 {
//                it?.list?.get(0)?.let {
//                    ad_two.setData(it)
//                    ad_two.getImageView().load(it.imgUrl)
//                }
//            }

        http().mApiService.home()
            .get3(next = {
                swipe.isRefreshing = false

                it?.hot?.let { it1 ->
                    adapter.setNewData(it1)
                }
                it?.newin?.let { it1 ->
                    adapterx.addData(it1)
                }
            }, err = {
                swipe.isRefreshing = false
                toast(it)
            })

    }

}