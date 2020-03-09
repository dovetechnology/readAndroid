package com.dove.readandroid.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.network.RxHttpUtil
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.get4
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.model.Book

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2020/3/6 11:35
 * ===============================
 */
class StartReadBook(
    var context: Context,
    var name: String = "",
    var id: String = "",
    var book: Book? = null
) {


    fun start() {
        if (book != null && book?.novelList != null) {
            (context as BaseMvcActivity).start(ReadActivity::class.java, Bundle().apply {
                putSerializable("data", book)
            })
        } else {
            book = App.instance.db.getBookDao().find(name)
            if (book != null) {
                book?.novelList = App.instance.db.getChapDao().findChap(name)
                (context as BaseMvcActivity).start(ReadActivity::class.java, Bundle().apply {
                    putSerializable("data", book)
                })
            } else {
                //从网络获取
                http().mApiService.open(id)
                    .compose(RxHttpUtil.handleResult2(context as LifecycleOwner))
                    .map {
                        var b = it.data.data
                        b.novelList?.forEach {
                            it.name = b.name //保存数据库 用书名来关联章节
                        }
                        it
                    }
                    .get4(isShowDialog = true) {
                        App.instance.db.getBookDao().add(it?.data)
                        App.instance.db.getChapDao().addAll(it?.data?.novelList)
                        //很重要     // 必须用 数据库查出来的 数据  不然那阅读数据 章节没法保存（因为主键id是自增）
                        var n = it?.data?.name
                        var mSections = App.instance.db.getChapDao().findChap(n)
                        it?.data?.novelList = mSections

                        (context as BaseMvcActivity).start(
                            ReadActivity::class.java,
                            Bundle().apply {
                                putSerializable("data", it?.data)
                            })
                    }

            }

        }
    }

}
