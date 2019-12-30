package com.dove.readandroid.ui

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.ext.load
import com.appbaselib.ext.toast
import com.appbaselib.utils.DialogUtils
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.model.Top
import com.leaf.library.StatusBarUtil
import com.safframework.ext.click
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.content_scrolling.*

class BookDetailActivity : BaseMvcActivity() {
    lateinit var top: String
    lateinit var book: Book
    lateinit var progressDialog: ProgressDialog
    override fun initView(mSavedInstanceState: Bundle?) {
        StatusBarUtil.setTransparentForWindow(this)
        StatusBarUtil.setDarkMode(this)
        icback.click {
            finish()
        }
        top = intent.getStringExtra("data")
        progressDialog = ProgressDialog.show(mContext, "", "加载中", false, true)
        http().mApiService.open(top, "")
            .get3(next = {
                progressDialog.dismiss()
                setValue(it?.data)
            }, err = {
                progressDialog.dismiss()
                toast(it)
            })
        tv_add.click {

            http().mApiService.addShujia(book.name, book.author, book.title)
                .get3 (isShowDialog = true){
                    toast("已加入书架")
                }
        }
        tv_start.click {
            start(ReadActivity::class.java)
        }
    }

    private fun setValue(it: Book?) {
        it?.let {
            book = it
            name.text = it.name
            type.text = it.stat
            auther.text = "作者：" + it.author
            time.text = "更新时间：" + it.updateTime
            jianjie.text = it.description
            iv_cover.load(it.coverImage)
        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_book_detail
    }


}
