package com.dove.readandroid.ui.me

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil.setContentView
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.ext.toast
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.safframework.ext.click
import kotlinx.android.synthetic.main.activity_wangji_pass.*
import kotlinx.android.synthetic.main.title_bar.*

class WangjiPassActivity : BaseMvcActivity() {
    override fun initView(mSavedInstanceState: Bundle?) {

        titleBar.setTitle("忘记密码")
        tv_denglu.click {
            http().mApiService.findpass(et_count.text.toString())
                .get3(isShowDialog = true) {
                    toast("密码已发送")
                    finish()
                }
        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_wangji_pass
    }


}
