package com.dove.readandroid.ui.me

import android.view.View
import com.appbaselib.base.BaseMvcFragment
import com.dove.readandroid.R
import com.dove.readandroid.ui.common.UserShell
import com.dove.readandroid.ui.shujia.LoginActivity
import com.safframework.ext.click
import kotlinx.android.synthetic.main.fragment_me.*

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class MeFragment : BaseMvcFragment() {


    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_me
    }

    override fun initView() {

        if (UserShell.getInstance().isLogin) {
            tvName.text = UserShell.getInstance().userBean.name
        }
        tvName.click {
            if (UserShell.getInstance().isLogin) {
                start(EditUserActivity::class.java)
            } else {
                start(LoginActivity::class.java)
            }
        }

    }

}