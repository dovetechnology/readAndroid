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
import com.appbaselib.utils.DateUtils
import com.appbaselib.utils.DialogUtils
import com.appbaselib.utils.LogUtils
import com.dove.readandroid.R
import com.dove.readandroid.event.ShujiaEvent
import com.dove.readandroid.event.UserEvent
import com.dove.readandroid.network.get4
import com.dove.readandroid.ui.*
import com.dove.readandroid.ui.common.Constants
import com.dove.readandroid.ui.common.UserShell
import com.dove.readandroid.ui.model.BookShelf
import com.dove.readandroid.ui.shucheng.BookShelfAdapter
import com.iflytek.cloud.msc.util.DataUtil
import com.safframework.ext.bluetoothAdapter
import kotlinx.android.synthetic.main.view_ad.*
import okhttp3.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.util.*
import kotlin.concurrent.thread
import java.text.SimpleDateFormat


/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class ShujiaFragment : BaseRefreshFragment<BookShelf>() {
    override fun initAdapter() {
        mAdapter = BookShelfAdapter(com.dove.readandroid.R.layout.item_shu, mList)
        recyclerview.layoutManager = GridLayoutManager(mContext, 3)
        mAdapter.setOnItemClickListener { adapter, view, position ->

            StartReadBook(mContext, mList.get(position).name, mList.get(position).articleId).start()

        }
        mAdapter.setOnItemLongClickListener { adapter, view, position ->
            DialogUtils.getDefaultDialog(mContext, "确定移除书架吗？") {

                http().mApiService.removeShujia(mList.get(position).caseId)
                    .get3(isShowDialog = true) {
                        App.instance.db.getBookDao().remove(mList.get(position).name)
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
        //获取网络书架信息
        swipe.isRefreshing = true
        mAdapter.loadMoreModule?.isEnableLoadMore = false
        mAdapter.loadMoreModule?.isAutoLoadMore = false
        mAdapter.loadMoreModule?.isEnableLoadMoreIfNotFullPage = false
        getData()

    }

    override fun getLoadingTargetView(): View? {
        return swipe
    }

    fun getData() {
        //优先展示本地的书架书本信息
        var datas = App.instance.db.getBookDao().getShujia()
//            ?.apply {
//            Collections.reverse(this)
//        }
        if (datas != null && datas.size != 0) {
            mList.clear()
            mAdapter.addData(datas)
            swipe.isRefreshing = false
        } else {
            //从网络获取
            requestData()
        }
        //广告
        http().mApiService.ad("2")
            .get3 {
                if (it != null && !it.list.isNullOrEmpty()) {
                    it.list.get(0).let {
                        ad.setData(it)
                    }
                }
            }
    }

    override fun requestData() {

        http().mApiService.shujiaList()
            .get3(next = {

                it?.forEach {
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    var time=sdf.parse(it.joindate)
                    it.joinTime=time.time
                    if (App.instance.db.getBookDao().findShelf(it.name) == null) {
                        App.instance.db.getBookDao().addShujia(it)
                    }
                }
                mList.clear()
                loadComplete(it)
            }, err = {
                loadError(it)
            }, complete = {
                swipe.isRefreshing = false
            })


    }


    override fun getContentViewLayoutID(): Int {
        return com.dove.readandroid.R.layout.fragment_shujia
    }

    override fun registerEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefresh(muservent: ShujiaEvent) {
        //信息更新了
        requestData()
//        var newBookShelf=BookShelf().apply {
//            this.caseId=muservent.mb.caseId
//            this.name=muservent.mb.name
//            this.articleId=muservent.mb.articleId
//            this.img=muservent.mb.coverImage
//        }
//        mAdapter.addData(0,newBookShelf)
        //  App.instance.db.getBookDao().addShelf()
    }

}