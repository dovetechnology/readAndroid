package com.dove.readandroid.ui.shucheng

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appbaselib.view.RatioImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dove.imuguang.base.BaseRefreshFragment
import com.dove.readandroid.R
import com.dove.readandroid.event.ButtonClick
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.BookDetailActivity
import com.dove.readandroid.ui.model.Book
import com.safframework.ext.click
import kotlinx.android.synthetic.main.fragment_fenlei_paihang_content.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PaihangContentFragment : BaseRefreshFragment<Book>() {
    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_fenlei_paihang_content;
    }
    fun toTop() {
        mRecyclerview.scrollToPosition(0)

    }

    override fun initView() {
        super.initView()
        toggleShowLoading(true)
        requestData()
        setLoadMoreListener()

        iv_up.click {
          toTop()

        }
        mRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                var p =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                if (p > 6) {
                    iv_up.visibility = View.VISIBLE
                } else {
                    iv_up.visibility = View.GONE
                }
            }
        })
    }

    override fun initAdapter() {
        mAdapter = PaihangContentAdapter(R.layout.item_paihang_content, mList)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            var viewHolder =
                mAdapter.weakRecyclerView.get()?.findViewHolderForLayoutPosition(position) as BaseViewHolder
            var image = viewHolder.getView<RatioImageView>(R.id.iv_shu)
            start(BookDetailActivity::class.java, Bundle().apply {
                putSerializable("data", Book().apply {
                    articleId = mList.get(position).articleId
                    name = mList.get(position).name
                })
            }, image, "book")
        }

    }

//    override fun getLayoutManager(): RecyclerView.LayoutManager {
//        return  GridLayoutManager(mContext, 3)
//    }

    override fun requestData() {

        http().mApiService.top(arguments!!.getString("data"), "2", pageNo, pageSize)
            .get3(next = {
                loadComplete(it?.list)

            }, err = {
                loadError(it)
            })
    }
}
