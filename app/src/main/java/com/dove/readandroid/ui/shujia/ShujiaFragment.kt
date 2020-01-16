package com.dove.readandroid.ui.shujia

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.dove.imuguang.base.BaseRefreshFragment
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.shucheng.HomeBookAdapter
import com.safframework.ext.click
import kotlinx.android.synthetic.main.fragment_shujia.*
import android.R
import com.appbaselib.utils.LogUtils
import com.dove.readandroid.ui.*
import okhttp3.*
import java.io.IOException
import kotlin.concurrent.thread


/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class ShujiaFragment : BaseRefreshFragment<Book>() {
    override fun initAdapter() {
        mAdapter = HomeBookAdapter(com.dove.readandroid.R.layout.item_shu, mList)
        recyclerview.layoutManager = GridLayoutManager(mContext, 3)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            var book = mList.get(position)


//            http().mApiService.openName(book.name, book.author, "")
//                .get3(isShowDialog = true) {
//                    start(ReadActivity::class.java, Bundle().apply {
//                        putSerializable("data", it?.data)
//                    })
//                }
            //改为本地获取
            book.novelList = App.instance.db.getChapDao().findChap(book.name)
            if (book.novelList == null || book.novelList.size == 0) {
                //获取 book
                http().mApiService.openName(book.name, book.author, "")
                    .get3(isShowDialog = true) {
                        start(BookDetailActivity::class.java, Bundle().apply {
                            putSerializable("data", it?.data)
                        })
                    }
//                start(BookDetailActivity::class.java, Bundle().apply {
//                    putSerializable("data", book)
//                })
            } else {
                start(ReadActivity::class.java, Bundle().apply {
                    putSerializable("data", book)
                })
            }
            //测试 Socket closed 的原因
            //           val url =
//                "http://test.imuguang.com/read/novel/open/name?name=${book.name}&author=${book.author}"
//         //   val okHttpClient = RetrofitHelper.getInstance().provideOkHttpClient(OkHttpClient.Builder())
//            var okHttpClient=OkHttpClient()
//            val request = Request.Builder()
//                .url(url)
//                .build()
//            thread {
//                var call = okHttpClient.newCall(request)
//                call.enqueue(object : Callback {
//                    override fun onFailure(call: Call, e: IOException) {
//                        LogUtils.e(e.message)
//
//                    }
//
//                    override fun onResponse(call: Call, response: Response) {
//                        LogUtils.e(response.message)
//                    }
//
//                })
//            }

        }
    }

    override fun onUserVisible() {
        super.onUserVisible()
        //可能信息更新了
        mList.clear()
        mList.addAll(App.instance.db.getBookDao().shujia())
        mAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        //可能信息更新了
        mList.clear()
        mList.addAll(App.instance.db.getBookDao().shujia())
        mAdapter.notifyDataSetChanged()

    }
    override fun initView() {
        super.initView()
        // toggleShowLoading(true)
        etSearch.click {
            start(SearchActivity::class.java)
        }
        mList.addAll(App.instance.db.getBookDao().shujia())
        refreshData(false)

        http().mApiService.ad("2")
            .get3 {

            }
    }

    override fun getLoadingTargetView(): View? {
        return swipe
    }

    override fun requestData() {
        http().mApiService.shujiaList()
            .get3 {
                if (it?.size != mList.size) {
                    loadComplete(it)
                } else {
                    mSwipeRefreshLayout?.isRefreshing = false
                }
            }
    }


    override fun getContentViewLayoutID(): Int {
        return com.dove.readandroid.R.layout.fragment_shujia
    }

}