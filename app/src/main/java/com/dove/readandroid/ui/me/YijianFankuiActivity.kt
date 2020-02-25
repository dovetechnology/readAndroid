package com.dove.readandroid.ui.me

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.ext.toast
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.safframework.ext.click
import com.safframework.ext.postDelayed
import com.safframework.ext.textChange
import kotlinx.android.synthetic.main.activity_yijian_fankui.*
import kotlinx.android.synthetic.main.title_bar.*
import kotlinx.coroutines.delay

class YijianFankuiActivity : BaseMvcActivity() {
    override fun initView(mSavedInstanceState: Bundle?) {
        titleBar.setTitle("意见反馈")
        et_yijian.textChange {
            tv_sure.isEnabled=it.isNotEmpty()
        }
        tv_sure.click {
            http().mApiService.fankui(et_yijian.text.toString())
                .get3 {
                    toast("提交成功")
                    postDelayed (time = 500){
                        finish()
                    }
                }
        }
    }

    override fun getContentViewLayoutID(): Int {
       return  R.layout.activity_yijian_fankui
    }

}
