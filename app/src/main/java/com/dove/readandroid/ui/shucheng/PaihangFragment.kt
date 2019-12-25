package com.dove.rea

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.appbaselib.base.BaseMvcFragment
import com.appbaselib.ext.toast
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
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
        rv_content.adapter = PaihangContentAdapter(R.layout.item_paihang_content, mutableListOf()).apply {
            contentAdapter=this
        }

        getData()
    }

    var list = arrayListOf<Top>()
    private fun getData() {

        http().mApiService.top()
            .get3(next = {
                list= it as ArrayList<Top>
                var titles = mutableListOf<String>()
                it?.forEach {
                    titles.add(it.topName)
                }
                (rv_title.adapter as PaihangTitleAdapter).addData(titles)
                contentAdapter.setNewData(list.get(0).totalList)

            }, err = {
                toast(it)
            })
    }

}