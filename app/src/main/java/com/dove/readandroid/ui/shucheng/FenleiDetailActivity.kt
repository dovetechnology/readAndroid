package com.dove.readandroid.ui.shucheng

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.base.Navigator
import com.appbaselib.common.load
import com.appbaselib.ext.toast
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.BookDetailActivity
import com.dove.readandroid.ui.model.Book
import com.dove.readandroid.ui.model.DetailDataWrap
import com.google.android.material.appbar.AppBarLayout
import com.leaf.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_fenlei_detail.*
import kotlinx.android.synthetic.main.title_bar.*

class FenleiDetailActivity : BaseMvcActivity() {
    lateinit var adapter: HomeBookAdapter
    var mList = arrayListOf<Book>()
    override fun initView(mSavedInstanceState: Bundle?) {

        titleBar.setTitle("精选小说")
        Navigator(supportFragmentManager,R.id.container).showFragment(FenleiDetailFragment().apply {
            arguments=Bundle()
                .apply {
                    putString("data",intent.getStringExtra("data"))
                    putString("title",intent.getStringExtra("title"))
                }
        })
    }





    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_fenlei_detail
    }

}
