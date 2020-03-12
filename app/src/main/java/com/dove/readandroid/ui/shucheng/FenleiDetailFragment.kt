package com.dove.readandroid.ui.shucheng

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appbaselib.common.load
import com.appbaselib.view.RatioImageView
import com.dove.imuguang.base.BaseRefreshFragment
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.BookDetailActivity
import com.dove.readandroid.ui.model.Book
import com.safframework.ext.click

class FenleiDetailFragment : BaseRefreshFragment<Book>() {

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_fenlei_detail
    }

    override fun initView() {
        super.initView()

//        app_bar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
//            override fun onOffsetChanged(p0: AppBarLayout?, i: Int) {
//                if (i >= 0) {
//                    swipe.setEnabled(true); //当滑动到顶部的时候开启
//                } else {
//                    swipe.setEnabled(false); //否则关闭
//                }
//
//            }
//        })

        mAdapter.setOnItemClickListener { adapter, view, position ->
            start(BookDetailActivity::class.java, Bundle().apply {
                putSerializable("data", mList.get(position))
            })
        }
        toggleShowLoading(true)
        setLoadMoreListener()
        requestData()

    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(mContext, 3)
    }

    override fun initAdapter() {
        mAdapter = FenletDetailBookAdapter(R.layout.item_shu, mList)

    }

    override fun requestData() {

        arguments?.getString("data")?.let {
            http().mApiService.fenleiDetail(it, "2", pageNo, pageSize)
                .get3(isShowDialog = false, next = {

                    if (isFirstReresh)
                    {
                        if (it != null && it.list != null && it.list.get(0) != null) {
                            var view =
                                layoutInflater.inflate(R.layout.view_fenlei_detail_title, null, false)
                            view.findViewById<RatioImageView>(R.id.iv_fengmian)
                                .load(it?.list.get(0).coverImage)
                            view.findViewById<TextView>(R.id.name).text = it?.list.get(0).name
                            view.findViewById<TextView>(R.id.tv_jianjie).text =
                                it?.list.get(0).description
                            view.findViewById<TextView>(R.id.tv_type).text =
                                arguments?.getString("title")
                            view.findViewById<LinearLayout>(R.id.toolbar_layout).click {
                                start(BookDetailActivity::class.java, Bundle().apply {
                                    putSerializable("data", mList.get(0))
                                })
                            }
                            mAdapter.addHeaderView(view)
                        }
                    }

                    loadComplete(it?.list)
                }, err = {
                    loadError(it)
                })
        }
    }

    private fun setData(it: List<Book>?) {
//        it?.let {
//            it.hot?.let {
//
//            }
//            if (it.hot != null && it.hot.size >= 1) {
//                iv_fengmian.load(it.hot.get(0).coverImage)
//                name.text = it.hot.get(0).name
//                tv_jianjie.text = it.hot.get(0).description
//                mList.addAll(it.hot)
//            }
//            it.top?.let {
//                mList.addAll(it)
//            }
//            it.update?.let {
//                mList.addAll(it)
//            }
//            loadComplete(null)
//        }
    }

}