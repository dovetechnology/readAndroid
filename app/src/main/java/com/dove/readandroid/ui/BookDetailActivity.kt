package com.dove.readandroid.ui

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.common.load
import com.appbaselib.ext.toast
import com.appbaselib.network.RxHttpUtil
import com.dove.readandroid.R
import com.dove.readandroid.event.ShujiaEvent
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.get4
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.model.Book
import com.leaf.library.StatusBarUtil
import com.safframework.ext.click
import com.safframework.ext.postDelayed
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.content_scrolling.*
import kotlinx.coroutines.delay
import org.greenrobot.eventbus.EventBus

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

            http().mApiService.addShujia(book.name, book.author, book.title)
                .get3(isShowDialog = true) {
                    toast("已加入书架")
                    book.isAddShlef = 1
                    App.instance.db.getBookDao().update(book)
                    //更新主页书架的信息
                    EventBus.getDefault().post(ShujiaEvent())

                }
        }
        tv_start.click {
            start(ReadActivity::class.java, Bundle().apply {
                putSerializable("data", book)
            })
            finish()
        }

//查看数据库有没有浏览过改书
        var b = App.instance.db.getBookDao().find(book.name)

        if (b == null) {
            //如果本地没有就从网络获取
            progressDialog = ProgressDialog.show(mContext, "", "加载中", false, true)

            http().mApiService.open(book.novelUrl)
                .compose(RxHttpUtil.handleResult2(mContext as LifecycleOwner))
                .map {
                    it.data.data.novelList?.forEach {
                        it.name = book.name //保存数据库 用书名来关联章节
                    }
                    it
                }
                .get4(next = {
                    progressDialog?.dismiss()
                    setValue(it?.data)
                    App.instance.db.getBookDao().add(book)
                    App.instance.db.getChapDao().addAll(book.novelList) //sb room数据库

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
                if (it!=null&&!it.list.isNullOrEmpty())
                {
                    it.list.get(0).let {
                        ad_c.setData(it)
                        ad_c.getImageView().load(it.imgUrl)
                    }
                }
            }
        http().mApiService.ad("6")
            .get3 {
                if (it!=null&&!it.list.isNullOrEmpty())
                {
                    it.list.get(0).let {
                        ad.setData(it)
                        ad.getImageView().load(it.imgUrl)
                    }
                }

            }
        setValue(book)
    }

    private fun setValue(it: Book?) {
        it?.let {
            book = it
            name.text = it.name
            type.text = it.stat
            auther.text = "作者：" + it.author
            time.text = "更新时间：" + it.updateTime
            jianjie.text = it.description
            if (!it.coverImage.isNullOrEmpty())
                iv_cover.load(it.coverImage)
        }
        //loaddata
        //加载目录
        var titles = arrayListOf<String>()

        it?.novelList?.let {
            it?.forEachIndexed { index, book ->
//                if (index > 10) {
//                    return@forEachIndexed
//                }
                titles.add(book.title)
            }
            rv_mulu.layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            rv_mulu.adapter = MuluAdapter(R.layout.item_mulu, titles)
        }


    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_book_detail
    }


}
