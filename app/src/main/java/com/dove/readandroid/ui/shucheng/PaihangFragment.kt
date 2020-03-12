package com.dove.rea

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.appbaselib.base.BaseMvcFragment
import com.appbaselib.base.Navigator
import com.appbaselib.ext.toast
import com.appbaselib.network.RxHttpUtil
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.get4
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.BookDetailActivity
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.model.Fenlei
import com.dove.readandroid.ui.model.Top
import com.dove.readandroid.ui.shucheng.PaihangContentAdapter
import com.dove.readandroid.ui.shucheng.PaihangContentFragment
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

    var titles = mutableListOf<Top>()
    var map = hashMapOf<Int, PaihangContentFragment>()
    lateinit var navigator: Navigator
    var p = 0

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_paihang
    }

    lateinit var titleAdapter: PaihangTitleAdapter

    override fun initView() {

        navigator = Navigator(childFragmentManager, R.id.content)
        rv_title.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        rv_title.adapter = PaihangTitleAdapter(R.layout.item_paihang_title, titles).apply {
            titleAdapter = this
        }
        titleAdapter.setOnItemClickListener { adapter, view, position ->
            p = position
            if (!map.containsKey(position)) {
                map.put(position, PaihangContentFragment().apply {
                    arguments = Bundle().apply {
                        putString("data", titles.get(position).id)
                    }
                })
            }
            titleAdapter.setSingleChoosed(position)
            navigator.showFragment(map.get(position))

        }


        getData()
        swipe.setOnRefreshListener {
            getData()
        }
    }

    override fun getLoadingTargetView(): View {
        return swipe
    }

    private fun getData() {

        http().mApiService.paihangTag()
            .compose(RxHttpUtil.handleResult2(mContext as LifecycleOwner))
//            .map {
//                it.data.forEach {
//                    var s = it.name.substring(0..1)
//                    it.name = it.name.toString().replace(s, s + "\n")
//                }
//                it
//            }
            .get4(next = {
                swipe.isRefreshing = false
                swipe.isEnabled = false
                it?.let { it1 -> titles.addAll(it1) }
                titleAdapter.notifyDataSetChanged()
                //默认第一个排行
                map.put(0, PaihangContentFragment().apply {
                    arguments = Bundle().apply {
                        putString("data", titles.get(0).id)
                    }
                })
                titleAdapter.setSingleChoosed(0)
                navigator.showFragment(map.get(0))

            }, err = {
                toast(it)
                swipe.isRefreshing = false
                swipe.isEnabled = false

            })


    }

    fun toTop() {
        map.get(p)?.toTop()

    }


}