package com.dove.readandroid.ui.me

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.utils.PreferenceUtils
import com.dove.readandroid.R
import com.dove.readandroid.ui.common.Constants
import com.dove.readandroid.utils.AppConfig
import com.dove.readandroid.utils.CleanDataUtils
import com.safframework.ext.click
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.title_bar.*

class SettingActivity : BaseMvcActivity() {
    override fun initView(mSavedInstanceState: Bundle?) {
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
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_setting
    }


}
