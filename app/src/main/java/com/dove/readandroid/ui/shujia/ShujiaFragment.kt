package com.dove.readandroid.ui.shujia

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.dove.imuguang.base.BaseRefreshFragment
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.shucheng.HomeBookAdapter
import com.safframework.ext.click
import kotlinx.android.synthetic.main.fragment_shujia.*
import com.appbaselib.common.load
import com.appbaselib.network.RxHttpUtil
import com.appbaselib.utils.LogUtils
import com.dove.readandroid.R
import com.dove.readandroid.event.ShujiaEvent
import com.dove.readandroid.event.UserEvent
import com.dove.readandroid.network.get4
import com.dove.readandroid.ui.*
import com.dove.readandroid.ui.common.Constants
import com.dove.readandroid.ui.common.UserShell
import kotlinx.android.synthetic.main.view_ad.*
import okhttp3.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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


            http().mApiService.open(book.articleId)
                .get3(isShowDialog = true) {
                    var list = App.instance.db.getChapDao().findChap(book.name)
                    if (list != null&&list.size!=0) {
                        it?.data?.novelList = list  //本地可能缓存过一些章节
                    }

                    start(ReadActivity::class.java, Bundle().apply {
                        putSerializable("data", it?.data)
                    })
                }
            //改为本地获取
//            book.novelList = App.instance.db.getChapDao().findChap(book.name)
//            if (book.novelList != null && book.novelList.size != 0 && !book.novelUrl.isNullOrEmpty()) {
//
//                start(ReadActivity::class.java, Bundle().apply {
//                    putSerializable("data", book)
//                })
////                start(BookDetailActivity::class.java, Bundle().apply {
////                    putSerializable("data", book)
////                })
//            } else {
//                //获取 book
//                http().mApiService.openName(book.name, book.author, "")
////                    .compose(RxHttpUtil.handleResult2(mContext as LifecycleOwner))
////                    .map {
////
////                    }
//                    .get3(isShowDialog = true) {
//                        //补充书的信息
//                        var oldBook = App.instance.db.getBookDao().find(book.name)
//                        oldBook.novelUrl = it?.data?.novelUrl
//                        oldBook.novelList = it?.data?.novelList
//                        mList.set(position, oldBook)
//                        App.instance.db.getBookDao().update(oldBook)
//                        onRefresh(null)
//                        start(ReadActivity::class.java, Bundle().apply {
//                            putSerializable("data", it?.data)
//                        })
//                    }
//            }
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


    override fun initView() {
        super.initView()
        // toggleShowLoading(true)
        etSearch.click {
            start(SearchActivity::class.java)
        }

        //广告 有没有登陆都加载广告
        http().mApiService.ad("2")
            .get3 {
                if (it != null && !it.list.isNullOrEmpty()) {
                    it.list.get(0).let {
                        ad.setData(it)
                    }
                }

            }

        if (!UserShell.getInstance().isLogin) {
            return
        }
//        //优先展示本地的书架书本信息
//        var datas = App.instance.db.getBookDao().shujia()
//        if (datas != null && datas.size != 0) {
//            mAdapter.addData(App.instance.db.getBookDao().shujia())
//        }
        //获取网络书架信息
        swipe.isRefreshing = true
        requestData()

    }

    override fun getLoadingTargetView(): View? {
        return swipe
    }

    override fun requestData() {

        //广告
        http().mApiService.ad("2")
            .get3 {
                if (it != null && !it.list.isNullOrEmpty()) {
                    it.list.get(0).let {
                        ad.setData(it)
                    }
                }
            }

        http().mApiService.shujiaList()
            .compose(RxHttpUtil.handleResult2(mContext as LifecycleOwner))
            .map {
                var datas = arrayListOf<Book>()
                datas.addAll(it.data)

                it.data?.forEachIndexed { index, book ->
                    var title = book.name
                    var book = App.instance.db.getBookDao().find(title)
                    book?.apply {
                        datas.set(index, book)
                    }
                }
                it.data = datas
                it
            }
            .get4(next = {
                // App.instance.db.getBookDao().addAll(it)
                mList.clear()
                loadComplete(it)
            }, err = {
                loadError(it)
            }, complete = {
                swipe.isRefreshing = false
            })

    }


    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_shujia
    }

    override fun registerEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefresh(muservent: ShujiaEvent?) {
        //信息更新了
        requestData()
    }

}