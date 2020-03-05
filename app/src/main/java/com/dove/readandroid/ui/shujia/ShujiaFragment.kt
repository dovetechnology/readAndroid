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
import com.appbaselib.utils.DialogUtils
import com.appbaselib.utils.LogUtils
import com.dove.readandroid.R
import com.dove.readandroid.event.ShujiaEvent
import com.dove.readandroid.event.UserEvent
import com.dove.readandroid.network.get4
import com.dove.readandroid.ui.*
import com.dove.readandroid.ui.common.Constants
import com.dove.readandroid.ui.common.UserShell
import com.safframework.ext.bluetoothAdapter
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
            //            http().mApiService.open(book.articleId)
//                .get3(isShowDialog = true) {
//                    var list = App.instance.db.getChapDao().findChap(book.name)
//                    if (list != null && list.size != 0) {
//                        it?.data?.novelList = list  //本地可能缓存过一些章节
//                    }
//
//                    start(ReadActivity::class.java, Bundle().apply {
//                        putSerializable("data", it?.data)
//                    })
//                }
            //改为优先本地获取
            var book = mList.get(position)
            if (App.instance.db.getBookDao().find(book.name) != null) {
                //封面图非空 所以数据库存在书
                book = App.instance.db.getBookDao().find(book.name)
                book.novelList = App.instance.db.getChapDao().findChap(book.name)

                start(ReadActivity::class.java, Bundle().apply {
                    putSerializable("data", book)
                })
            } else {
                //获取 book
                http().mApiService.open(book.articleId)
                    .compose(RxHttpUtil.handleResult2(mContext as LifecycleOwner))
                    .map {
                        it.data.data.novelList?.forEach {
                            it.name = book.name //保存数据库 用书名来关联章节
                        }

                        it
                    }
                    .get4(isShowDialog = true) {
//                        var list = App.instance.db.getChapDao().findChap(book.name)
//                        if (list != null && list.size != 0) {
//                            it?.data?.novelList = list  //本地可能缓存过一些章节
//                        }
                        App.instance.db.getBookDao().add(it?.data)
                        App.instance.db.getChapDao().addAll(it?.data?.novelList)
                        // 必须用 数据库查出来的 数据  不然那阅读数据没法保存
                        it?.data?.novelList=   App.instance.db.getChapDao().findChap(it?.data?.name)

                        mList.set(position,it?.data!!)
                        mAdapter.notifyDataSetChanged() //刷新章节

                        start(ReadActivity::class.java, Bundle().apply {
                            putSerializable("data", it?.data)
                        })
                    }
            }

        }
        mAdapter.setOnItemLongClickListener { adapter, view, position ->
            DialogUtils.getDefaultDialog(mContext, "确定移除书架吗？") {

                http().mApiService.removeShujia(mList.get(position).caseId)
                    .get3(isShowDialog = true) {
                        mAdapter.remove(position)
                    }

            }.show()
            return@setOnItemLongClickListener true
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
        mAdapter.loadMoreModule?.isEnableLoadMore = false
        mAdapter.loadMoreModule?.isAutoLoadMore = false
        mAdapter.loadMoreModule?.isEnableLoadMoreIfNotFullPage = false
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
                datas.addAll(it.data) //服务器的数据 书本信息不全  需要和本地数据融合

                it.data?.forEachIndexed { index, b ->
                    //书架返回的book里面有 书架id
                    var title = b.name
                    var book = App.instance.db.getBookDao().find(title) //因为本地存的书本信息更多一点
                    book?.let {
                        book.caseId = b.caseId
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