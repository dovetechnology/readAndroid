package com.dove.readandroid.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.common.load
import com.appbaselib.ext.toast
import com.appbaselib.network.RxHttpUtil
import com.chad.library.adapter.base.entity.SectionEntity
import com.dove.readandroid.R
import com.dove.readandroid.event.ShujiaEvent
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.get4
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.model.BookSectionItem
import com.leaf.library.StatusBarUtil
import com.safframework.ext.click
import com.safframework.ext.postDelayed
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.book_detail_content_scrolling.*
import org.greenrobot.eventbus.EventBus

class BookDetailActivity : BaseMvcActivity() {
    lateinit var book: Book //传过来的书 可能信息不完整
    var page = 1;
    var pageSize = 20;
    var isSave = false
    override fun getLoadingTargetView(): View? {
        return rv_mulu
    }

    override fun initView(mSavedInstanceState: Bundle?) {
        StatusBarUtil.setTransparentForWindow(this)
        StatusBarUtil.setDarkMode(this)
        icback.click {
            finish()
        }
        book = intent.getSerializableExtra("data") as Book
        //
        if (book.novelList == null) {
            book.novelList = mutableListOf()//初始化一下
        }
        tv_add.click {

            http().mApiService.addShujia(book.articleId, book.chapterId)
                .get3(isShowDialog = true) {
                    toast("已加入书架")
                    //更新主页书架的信息
                    EventBus.getDefault().post(ShujiaEvent(book))

                }
        }
        tv_start.click {
            startReadBook()
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
        rv_mulu.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        rv_mulu.adapter = MuluAdapter(R.layout.item_mulu, mutableListOf()).apply {
            muluAdapter = this
            setOnItemClickListener { adapter, view, position ->
                book.currentSetion = position
                startReadBook()
            }
        }
        rv_mulu.isNestedScrollingEnabled = false
        setValue(book)//先展示一部分信息
//查看数据库有没有浏览过改书
        var b = App.instance.db.getBookDao().find(book.name)

        if (b == null) {
            //如果本地没有就从网络获取
            //   progressDialog = ProgressDialog.show(mContext, "", "加载中", false, true)
            isSave = false
            toggleShowLoading(true, "目录加载中……")
            addChapter()

        } else {
            //加载本地
            isSave = true
            book = b
            book.novelList = App.instance.db.getChapDao().findChap(book.name)
            addBenDiMuluData()
        }
        //广告1/2
        http().mApiService.ad("5")
            .get3 {
                if (!it?.list.isNullOrEmpty()) {
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
                } else {
                    ad.visibility = View.GONE
                }

            }

        ns.setOnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                //滑动到底部
                if (isSave)
                    addBenDiMuluData()
                else
                    addChapter()
            }
        }
    }

    //加载目录
    fun addChapter() {
        http().mApiService.chapterList(book.articleId, page, pageSize)
            .get3 {
                toggleShowLoading(false)
                if (!it?.data?.list.isNullOrEmpty()) {
                    book.novelList.addAll(it?.data?.list!!)
                    addMuluData(it?.data?.list!!)
                    page++
                } else {
                    //加载完毕i
                }
            }

    }

    fun getBook() {
        http().mApiService.open(book.articleId)
            .compose(RxHttpUtil.handleResult2(mContext as LifecycleOwner))
            .map {
                it.data.data.novelList?.forEach {
                    it.name = book.name //保存数据库 用书名来关联章节
                }
                it
            }
            .get4(isShowDialog = true, message = "正在获取书籍信息", next = {
                if (it?.data?.novelList == null || it.data.novelList.size == 0) {
                    toast("没找到该书本章节")
                }

                App.instance.db.getBookDao().add(it?.data)
                App.instance.db.getChapDao().addAll(it?.data?.novelList) //sb room数据库
                //很重要     // 必须用 数据库查出来的 数据  不然那阅读数据 章节没法保存（因为主键id是自增）
                it?.data?.novelList = App.instance.db.getChapDao().findChap(it?.data?.name)
                it?.data?.currentSetion = book.currentSetion// 当点击目录中的条目 把位置加入进去
                book = it?.data!!
                postDelayed(time = 300) {
                    start(ReadActivity::class.java, Bundle().apply {
                        putSerializable("data", book)
                    })
                    finish()
                }

            }, err = {
                postDelayed(time = 300) {
                    finish()
                }
                toast(it)
            })
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
        }
    }

    lateinit var muluAdapter: MuluAdapter
    //当从网络加载的时候 加载目录信息
    fun addMuluData(list: MutableList<BookSectionItem>) {
        var titles = mutableListOf<String>()
        list?.forEach {
            titles.add(it.title)
        }
        muluAdapter.addData(titles)
    }

    //加载本地也分页 （当数据量太大  如2000条会卡顿）
    var p = 0;//目录现在的位置

    fun addBenDiMuluData() {
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

    fun startReadBook() {
        if (isSave) {
            start(ReadActivity::class.java, Bundle().apply {
                putSerializable("data", book)
            })
            finish()
        } else {
            getBook()
        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_book_detail
    }


}
