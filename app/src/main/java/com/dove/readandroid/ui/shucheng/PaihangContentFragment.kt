package com.dove.readandroid.ui.shucheng

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appbaselib.view.RatioImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dove.imuguang.base.BaseRefreshFragment
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.BookDetailActivity
import com.dove.readandroid.ui.model.Book

class PaihangContentFragment : BaseRefreshFragment<Book>() {


    override fun initView() {
        super.initView()
        toggleShowLoading(true)
        requestData()
        setLoadMoreListener()
    }
    override fun initAdapter() {
        mAdapter = PaihangContentAdapter(R.layout.item_paihang_content, mList)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            var viewHolder = mAdapter.weakRecyclerView.get()?.findViewHolderForLayoutPosition(position) as BaseViewHolder
            var image = viewHolder.getView<RatioImageView>(R.id.iv_shu)
            start(BookDetailActivity::class.java, Bundle().apply {
                putSerializable("data", Book().apply {
                    articleId = mList.get(position).articleId
                    name = mList.get(position).name
                })
            },image,"book")
        }

    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return  GridLayoutManager(mContext, 3)
    }

    override fun requestData() {

        http().mApiService.top(arguments!!.getString("data"), "2", pageNo, pageSize)
            .get3(next = {
                loadComplete(it?.list)

            }, err = {
                loadError(it)
            })
    }
}
