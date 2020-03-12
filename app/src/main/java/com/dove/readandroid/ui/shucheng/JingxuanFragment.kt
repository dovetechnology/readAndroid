package com.dove.rea

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.appbaselib.base.BaseMvcFragment
import com.appbaselib.common.load
import com.appbaselib.ext.toast
import com.appbaselib.view.RatioImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dove.readandroid.BannerImageloadr
import com.dove.readandroid.R
import com.dove.readandroid.event.ButtonClick
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.BookDetailActivity
import com.dove.readandroid.ui.OpenTypeHandler
import com.dove.readandroid.ui.model.AdData
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.shucheng.HomeBookAdapter
import com.dove.readandroid.ui.shucheng.RenqiBookAdapter
import com.dove.readandroid.ui.shucheng.ZuijinBookAdapter
import com.safframework.ext.click
import com.safframework.ext.postDelayed
import kotlinx.android.synthetic.main.fragment_jingxuan.*
import kotlinx.android.synthetic.main.fragment_shujia.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class JingxuanFragment(var next: () -> Unit) : BaseMvcFragment() {

    lateinit var adapter: HomeBookAdapter
    lateinit var adapterx: RenqiBookAdapter
    lateinit var adapterZuijin: ZuijinBookAdapter

    fun toTop() {
        nestscroll.smoothScrollTo(0,0)
    }

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

            var viewHolder =
                adapter.weakRecyclerView.get()?.findViewHolderForLayoutPosition(position) as BaseViewHolder
            var image = viewHolder.getView<RatioImageView>(R.id.iv_shu)

            start(BookDetailActivity::class.java, Bundle().apply {
                putSerializable("data", adapter.data.get(position))
            }, image, "book")
        }
        rv_xinshu.layoutManager = GridLayoutManager(mContext, 2)
        rv_xinshu.adapter = RenqiBookAdapter(R.layout.item_shu_renqi, arrayListOf()).apply {
            adapterx = this
        }
        adapterx.setOnItemClickListener { a, view, position ->

            var viewHolder =
                adapterx.weakRecyclerView.get()?.findViewHolderForLayoutPosition(position) as BaseViewHolder
            var image = viewHolder.getView<RatioImageView>(R.id.iv_shu)

            start(BookDetailActivity::class.java, Bundle().apply {
                putSerializable("data", adapterx.data.get(position))
            }, image, "book")
        }
        rv_zuijin.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        rv_zuijin.adapter = ZuijinBookAdapter(R.layout.item_shu_zuijin, arrayListOf()).apply {
            adapterZuijin = this
        }
        adapterZuijin.setOnItemClickListener { adapter, view, position ->

            var viewHolder =
                adapterZuijin.weakRecyclerView.get()?.findViewHolderForLayoutPosition(position) as BaseViewHolder
            var image = viewHolder.getView<RatioImageView>(R.id.iv_shu)

            start(BookDetailActivity::class.java, Bundle().apply {
                putSerializable("data", adapterZuijin.data.get(position))
            }, image, "book")
        }

        banner.setImageLoader(BannerImageloadr())
        tv_more.click {
            //刷新数据
            assembleData()
        }
        tv_more_renqi.click {
            next()
        }

        //   swipe.autoRefresh(200)
        getData()
        swipe.setOnRefreshListener {
            getData()
        }
        swipe.setOnLoadMoreListener {
            getLastData()
        }
        swipe.setEnableAutoLoadMore(false)
//        swipe.setOnAutoLoadListener {
//            getLastData()
//        }

    }

    var pageNo = 1  //当前页
    var pageSize = 10 //每页条数

    lateinit var adDatas: List<AdData>
    var hot: List<Book>? = null

    fun getLastData() {
        http().mApiService.lastupdate("2", pageNo, pageSize)
            .get3 {
                pageNo++
                it?.list?.let { it1 ->
                    swipe.finishRefresh()
                    if (it1.size == 0)
                        swipe.finishLoadMoreWithNoMoreData()
                    else
                        swipe.finishLoadMore()
                    postDelayed(300) {
                        adapterZuijin.addData(it1)
                    }
                }
            }
    }

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

                it?.hot?.let { it1 ->
                    hot = it1
                    assembleData()
                }
                it?.besthot?.let { it1 ->
                    adapterx.setNewData(it1)
                }

                //
                getLastData()

            }, err = {
                swipe.finishRefresh()
                toast(it)
            })

    }

    var tag = 0;
    private fun assembleData() {

        hot?.let {
            var data = mutableListOf<Book>()
            var x = 0
            while (x < 6) {
                if (tag < it.size) {
                    data.add(it.get(tag))
                    tag++
                } else {
                    tag = 0
                }
                x++
            }
            adapter.setNewData(data)
        }


    }



}