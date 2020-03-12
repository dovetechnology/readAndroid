package com.dove.rea

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.core.widget.TextViewCompat
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
import com.safframework.ext.inflateLayout
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
    lateinit var moreView: View
    var isAdd = true;
    fun toTop() {
        nestscroll.smoothScrollTo(0, 0)
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
        swipe.isRefreshing = true
        getData()
        swipe.setColorSchemeResources(R.color.colorAccent)
        swipe.setOnRefreshListener {
            getData()
        }
//        swipe.setOnAutoLoadListener {
//            getLastData()
//        }
        moreView = mContext.inflateLayout(R.layout.view_load_more, null, false)

        nestscroll.setOnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                //滑动到底部
                loadMore();
            }
        }
        rv_jingxuan.isNestedScrollingEnabled=false
        rv_zuijin.isNestedScrollingEnabled=false
        rv_xinshu.isNestedScrollingEnabled=false

    }

    private fun loadMore() {
        getLastData()
    }


    var pageNo = 1  //当前页
    var pageSize = 10 //每页条数

    lateinit var adDatas: List<AdData>
    var hot: List<Book>? = null

    fun getLastData() {
        http().mApiService.lastupdate("2", pageNo, pageSize)
            .get3(next = {
                pageNo++
                it?.list?.let { it1 ->
                    //    swipe.finishRefresh()
                    if (it1.size == 0) {
                        //加载完毕
                        lin_frame.removeView(moreView)
                        //               lin_frame.getChildAt(lin_frame.childCount-1)
                    }
                    //   swipe.finishLoadMoreWithNoMoreData()
                    //  swipe.finishLoadMore()
                    swipe.isRefreshing = false
                    adapterZuijin.addData(it1)
                }
                //增加尾部的footer
                if (isAdd) {
                    isAdd = false
                    lin_frame.addView(moreView)
                }
            }, err = {
                moreView.findViewById<TextView>(R.id.text).apply {
                    setText("加载失败,点击重新加载")
                    click {
                        getLastData()
                        setText("正在加载中...")
                    }
                }

            })
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
                // swipe.finishRefresh()
                swipe.isRefreshing = false
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