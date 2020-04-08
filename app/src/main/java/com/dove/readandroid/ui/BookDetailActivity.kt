package com.dove.readandroid.ui

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.common.load
import com.appbaselib.ext.toast
import com.appbaselib.network.RxHttpUtil
import com.appbaselib.utils.LogUtils
import com.dove.readandroid.R
import com.dove.readandroid.event.ShujiaEvent
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.get4
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.utils.runBackground
import com.leaf.library.StatusBarUtil
import com.safframework.ext.click
import com.safframework.ext.postDelayed
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.book_detail_content_scrolling.*
import kotlinx.coroutines.delay
import org.greenrobot.eventbus.EventBus
import kotlin.concurrent.thread

class BookDetailActivity : BaseMvcActivity() {
    lateinit var book: Book //传过来的书 可能信息不完整
    var progressDialog: ProgressDialog? = null
    override fun initView(mSavedInstanceState: Bundle?) {
        StatusBarUtil.setTransparentForWindow(this)
        StatusBarUtil.setDarkMode(this)
        icback.click {
            finish()
        }
        book = intent.getSerializableExtra("data") as Book
        //

        tv_add.click {

            http().mApiService.addShujia(book.articleId, book.chapterId)
                .get3(isShowDialog = true) {
                    toast("已加入书架")
                    //更新主页书架的信息
                    EventBus.getDefault().post(ShujiaEvent(book))

                }
        }
        tv_start.click {
            start(ReadActivity::class.java, Bundle().apply {
                putSerializable("data", book)
            })
            finish()
        }
        tv_more.click {
            if (it.text.equals("全文")) {
                jianjie.maxLines = 100
                tv_more.text = "收起"
            } else {
                jianjie.maxLines = 3
                tv_more.text = "全文"
            }
        }
//        if (jianjie.lineCount>3) {
//            tv_more.visibility = View.VISIBLE
//        } else {
//            tv_more.visibility = View.GONE
//
//        }

//查看数据库有没有浏览过改书
        var b = App.instance.db.getBookDao().find(book.name)

        if (b == null) {
            //如果本地没有就从网络获取
            //   progressDialog = ProgressDialog.show(mContext, "", "加载中", false, true)

            http().mApiService.open(book.articleId)
                .compose(RxHttpUtil.handleResult2(mContext as LifecycleOwner))
                .map {
                    it.data.data.novelList?.forEach {
                        it.name = book.name //保存数据库 用书名来关联章节
                    }
                    it
                }
                .get4(next = {
                    //     progressDialog?.dismiss()
                    App.instance.db.getBookDao().add(it?.data)
                    App.instance.db.getChapDao().addAll(it?.data?.novelList) //sb room数据库
                    //很重要     // 必须用 数据库查出来的 数据  不然那阅读数据 章节没法保存（因为主键id是自增）
                    it?.data?.novelList = App.instance.db.getChapDao().findChap(it?.data?.name)
                    setValue(it?.data)

                    if (it?.data?.novelList == null || it.data.novelList.size == 0) {
                        toast("没找到该书本章节")
                        finish()
                    }
                }, err = {
                    progressDialog?.dismiss()
                    postDelayed(time = 300) {
                        finish()
                    }
                    toast(it)
                })

        } else {
            //加载本地
            book = b
            book.novelList = App.instance.db.getChapDao().findChap(book.name)
        }
        //广告1/2
        http().mApiService.ad("5")
            .get3 {
                if ( !it?.list.isNullOrEmpty()) {
                    it?.list?.get(0).let {
                        ad_c.setData(it!!)
                    }
                }
            }
        http().mApiService.ad("6")
            .get3 {
                if (it != null && !it.list.isNullOrEmpty()) {
                    it.list.get(0).let {
                        ad.setData(it)
                    }
                }
                else{
                    ad.visibility=View.GONE
                }

            }
        rv_mulu.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        rv_mulu.adapter = MuluAdapter(R.layout.item_mulu, mutableListOf()).apply {
            muluAdapter = this
            setOnItemClickListener { adapter, view, position ->
                book.currentSetion = position
                start(ReadActivity::class.java, Bundle().apply {
                    putSerializable("data", book)
                })
            }
        }
        rv_mulu.isNestedScrollingEnabled=false
        ns.setOnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                //滑动到底部
                if (p < book.novelList?.size ?: 0) {
                    addMuluData();
                }
            }
        }
        setValue(book)
    }


    private fun setValue(mb: Book?) {
        mb?.let {
            book = it
            name.text = it.name
            type.text = it.stat
            auther.text = "作者：" + it.author
            time.text = "更新时间：" + it.updateTime
            jianjie.text = it.description
            if (!it.coverImage.isNullOrEmpty())
                iv_cover.load(it.coverImage)
            addMuluData()
        }
        //loaddata
        //加载目录
    }

    var p = 0;//目录现在的位置
    lateinit var muluAdapter: MuluAdapter
    fun addMuluData() {
        book.novelList?.let {

            if (p < it.size) {
                var i = 20;//加载的条数
                //继续加载目录
                if (it.size - p < 20) {
                    i = it.size - p  //剩余条数不够
                }
                var titles = mutableListOf<String>()
                for (index in 1..i) {
                    titles.add(it.get(p++).title)
                }
                muluAdapter.addData(titles)
            } else {

            }
        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_book_detail
    }


}
