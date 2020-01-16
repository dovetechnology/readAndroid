package com.dove.readandroid.ui.shucheng

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.common.load
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.BookDetailActivity
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.model.DetailDataWrap
import com.google.android.material.appbar.AppBarLayout
import com.leaf.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_fenlei_detail.*
import kotlinx.android.synthetic.main.title_bar.*

class FenleiDetailActivity : BaseMvcActivity() {
    lateinit var adapter: HomeBookAdapter
    var mList= arrayListOf<Book>()
    override fun initView(mSavedInstanceState: Bundle?) {

        titleBar.setTitle("精选小说")
        tv_type.text=intent.getStringExtra("title")
        recyclerview.layoutManager=GridLayoutManager(mContext,3)
        adapter = HomeBookAdapter(R.layout.item_shu, mList)
        recyclerview.adapter=adapter

        app_bar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(p0: AppBarLayout?, i: Int) {
                if (i >= 0) {
                    swipe.setEnabled(true); //当滑动到顶部的时候开启
                } else {
                    swipe.setEnabled(false); //否则关闭
                }

            }
        })
        swipe.setOnRefreshListener {
            getData()
        }
        adapter.setOnItemClickListener { adapter, view, position ->
            start(BookDetailActivity::class.java, Bundle().apply {
                putSerializable("data", mList.get(position))
            })
        }
        swipe.isRefreshing=true
        getData()
    }

    private fun getData() {

        http().mApiService.jingxuan(intent.getStringExtra("data")?:"")
            .get3 {
                swipe.isRefreshing=false
                setData(it)
            }
    }

    private fun setData(it: DetailDataWrap?) {
        it?.let {
            it.hot?.let {

            }
            if (it.hot!=null&&it.hot.size>=1)
            {
                iv_fengmian.load(it.hot.get(0).coverImage)
                name.text=it.hot.get(0).name
                tv_jianjie.text=it.hot.get(0).description
                mList.addAll(it.hot)
            }
            it.top?.let {
                mList.addAll(it)
            }
            it.update?.let {
                mList.addAll(it)
            }
        }
    }

    override fun getContentViewLayoutID(): Int {
      return  R.layout.activity_fenlei_detail
    }

}
