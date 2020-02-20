package com.dove.readandroid.ui.me

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appbaselib.base.BaseMvcActivity
import com.dove.readandroid.R
import kotlinx.android.synthetic.main.title_bar.*

class YijianFankuiActivity : BaseMvcActivity() {
    override fun initView(mSavedInstanceState: Bundle?) {
        titleBar.setTitle("意见反馈")

    }

    override fun getContentViewLayoutID(): Int {
       return  R.layout.activity_yijian_fankui
    }

}
