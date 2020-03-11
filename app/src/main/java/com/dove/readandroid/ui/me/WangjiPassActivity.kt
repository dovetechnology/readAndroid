package com.dove.readandroid.ui.me

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil.setContentView
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.ext.toast
import com.appbaselib.utils.PreferenceUtils
import com.dove.readandroid.R
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.WebViewActivity
import com.dove.readandroid.ui.common.Constants
import com.dove.readandroid.ui.model.AppData
import com.framework.ext.clickSpan
import com.framework.ext.colorSpan
import com.safframework.ext.click
import kotlinx.android.synthetic.main.activity_wangji_pass.*
import kotlinx.android.synthetic.main.title_bar.*

class WangjiPassActivity : BaseMvcActivity() {
     var appData: AppData?=null

    override fun initView(mSavedInstanceState: Bundle?) {

        appData =
            PreferenceUtils.getObjectFromGson(mContext, Constants.APPDATA, AppData::class.java)
        titleBar.setTitle("找回密码")
//        tv_denglu.click {
//            http().mApiService.findpass(et_count.text.toString())
//                .get3(isShowDialog = true) {
//                    toast("密码已发送")
//                    finish()
//                }
//        }
        appData?.let {
            tvmima.text = "请登录网站${it.findPassWebsite}找回密码"
            var p = tvmima.text.toString().length - 4
            tvmima.colorSpan(
                tvmima.text.toString(),
                5..p,
                ContextCompat.getColor(mContext, R.color.colorAccent)
            )
            tvmima.clickSpan(
                tvmima.text.toString(),
                5..p,
                ContextCompat.getColor(mContext, R.color.colorAccent),
                true,
                object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        WebViewActivity.instance(it.findPassWebsite, mContext)
                    }
                })
        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_wangji_pass
    }


}
