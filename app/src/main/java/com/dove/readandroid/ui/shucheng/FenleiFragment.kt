package com.dove.rea

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.appbaselib.base.BaseMvcFragment
import com.appbaselib.base.Navigator
import com.appbaselib.ext.toast
import com.appbaselib.network.RxHttpUtil
import com.appbaselib.view.RatioImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dove.imuguang.base.BaseRefreshFragment
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.get4
import com.dove.readandroid.ui.model.Fenlei
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.shucheng.*
import com.safframework.ext.dp2px
import com.safframework.ext.screenWidth
import kotlinx.android.synthetic.main.fragment_fenlei.*

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class FenleiFragment : BaseMvcFragment() {

    var titles = mutableListOf<Fenlei>()
    var map = hashMapOf<Int, FenleiContentFragment>()
    lateinit var navigator: Navigator
    var p=0
    lateinit var titleAdapter: FenleiTitleAdapter

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_fenlei
    }

    fun toTop() {
        map.get(p)?.toTop()
    }

    var width = 0;
    override fun initView() {
        // recyclerview.layoutManager = GridLayoutManager(mContext, 2)

        navigator = Navigator(childFragmentManager, R.id.content)
        rv_title.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        rv_title.adapter = FenleiTitleAdapter(R.layout.item_paihang_title, titles).apply {
            titleAdapter = this
        }
        titleAdapter.setOnItemClickListener { adapter, view, position ->
            p=position
            if (!map.containsKey(position)) {
                map.put(position, FenleiContentFragment().apply {
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

        http().mApiService.tag()
            .compose(RxHttpUtil.handleResult2(mContext as LifecycleOwner))
            .map {
                it.data.forEach {
                    var s = it.name.substring(0..1)
                    it.name = it.name.toString().replace(s, s + "\n")
                }
                it
            }
            .get4(next = {
                swipe.isRefreshing = false
                swipe.isEnabled = false
                it?.let { it1 -> titles.addAll(it1) }
                titleAdapter.notifyDataSetChanged()
                //默认第一个排行
                map.put(0, FenleiContentFragment().apply {
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




}