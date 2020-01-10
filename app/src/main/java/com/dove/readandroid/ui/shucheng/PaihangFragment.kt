package com.dove.rea

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.appbaselib.base.BaseMvcFragment
import com.appbaselib.ext.toast
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.BookDetailActivity
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.model.Top
import com.dove.readandroid.ui.shucheng.PaihangContentAdapter
import com.dove.readandroid.ui.shucheng.PaihangTitleAdapter
import kotlinx.android.synthetic.main.fragment_paihang.*

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class PaihangFragment : BaseMvcFragment() {


    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_paihang
    }

    lateinit var titleAdapter: PaihangTitleAdapter
    lateinit var contentAdapter: PaihangContentAdapter

    override fun initView() {

        rv_title.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        rv_title.adapter = PaihangTitleAdapter(R.layout.item_paihang_title, mutableListOf()).apply {
            titleAdapter = this
        }
        titleAdapter.setOnItemClickListener { adapter, view, position ->
            contentAdapter.setNewData(list.get(position).totalList)
        }

        rv_content.layoutManager = GridLayoutManager(mContext, 3)
        rv_content.adapter =
            PaihangContentAdapter(R.layout.item_paihang_content, mutableListOf()).apply {
                contentAdapter = this
            }
        contentAdapter.setOnItemClickListener { adapter, view, position ->

            start(BookDetailActivity::class.java, Bundle().apply {
                putSerializable("data", Book().apply {
                    novelUrl=contentAdapter.data.get(position).novelUrl
                    name=contentAdapter.data.get(position).novelName
                })
            })
        }
        toggleShowLoading(true)
        getData()
        swipe.setOnRefreshListener {
            getData()
        }
    }

    override fun getLoadingTargetView(): View {
        return swipe
    }

    var list = arrayListOf<Top>()
    private fun getData() {

        http().mApiService.top()
            .get3(next = {
                toggleShowLoading(false)
                swipe.isRefreshing=false
                list = it as ArrayList<Top>
                var titles = mutableListOf<String>()
                it?.forEach {
                    titles.add(it.topName)
                }
                (rv_title.adapter as PaihangTitleAdapter).addData(titles)
                contentAdapter.setNewData(list.get(0).totalList)

            }, err = {
                toggleShowLoading(false)
                toast(it)
            })
    }

}