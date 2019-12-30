package com.dove.readandroid.ui.shucheng

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appbaselib.base.BaseMvcActivity
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.leaf.library.StatusBarUtil
import kotlinx.android.synthetic.main.title_bar.*

class FenleiDetailActivity : BaseMvcActivity() {
    override fun initView(mSavedInstanceState: Bundle?) {

        titleBar.setTitle("精选小说")
        getData()

    }

    private fun getData() {

        http().mApiService.jingxuan(intent.getStringExtra("data")?:"")
            .get3 {

            }
    }

    override fun getContentViewLayoutID(): Int {
      return  R.layout.activity_fenlei_detail
    }

}
