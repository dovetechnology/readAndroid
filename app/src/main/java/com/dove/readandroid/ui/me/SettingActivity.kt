package com.dove.readandroid.ui.me

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.utils.PreferenceUtils
import com.dove.readandroid.R
import com.dove.readandroid.ui.WebViewActivity
import com.dove.readandroid.ui.common.Constants
import com.dove.readandroid.ui.common.UserShell
import com.dove.readandroid.ui.model.AppData
import com.dove.readandroid.ui.shujia.LoginActivity
import com.dove.readandroid.utils.AppConfig
import com.dove.readandroid.utils.CleanDataUtils
import com.safframework.ext.click
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.title_bar.*

class SettingActivity : BaseMvcActivity() {
    var appData: AppData? = null

    override fun initView(mSavedInstanceState: Bundle?) {

        appData = PreferenceUtils.getObjectFromGson(mContext, Constants.APPDATA, AppData::class.java)

        titleBar.titleTextView.text = "设置"
        tv_huancun.setText(CleanDataUtils.getTotalCacheSize(mContext))

        ll_hunacun.click {
            AlertDialog.Builder(mContext).setMessage("清除缓存吗")
                .setNegativeButton("取消", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.dismiss()
                    }

                })
                .setPositiveButton("确定", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        CleanDataUtils.clearAllCache(mContext)
                        tv_huancun.setText(CleanDataUtils.getTotalCacheSize(mContext))
                    }

                }).show()

        }
        sc.isChecked = AppConfig.isNightMode()
        sc.setOnCheckedChangeListener { buttonView, isChecked ->
            PreferenceUtils.setPrefBoolean(mContext, Constants.IS_BLACK, isChecked)
            AppConfig.setNightMode(isChecked)
        }
        if (UserShell.getInstance().isLogin) {
            tvExitLogin.visibility=View.VISIBLE
        } else {
            tvExitLogin.visibility=View.GONE
        }
        tvExitLogin.click {

            AlertDialog.Builder(mContext).setMessage("确定退出登录吗？")
                .setNegativeButton("取消", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.dismiss()
                    }

                })
                .setPositiveButton("确定", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        UserShell.getInstance().exitLogin()
                        val intent = Intent(mContext, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }

                }).show()


        }
        ll_xieyi.click {
            WebViewActivity.instance(appData?.userAgreement,mContext)

        }

        ll_yinsi.click {

            WebViewActivity.instance(appData?.privacyPolicies,mContext)

        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_setting
    }


}
