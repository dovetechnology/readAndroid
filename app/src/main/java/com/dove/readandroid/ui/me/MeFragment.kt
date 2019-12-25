package com.dove.readandroid.ui.me

import android.view.View
import androidx.annotation.MainThread
import com.appbaselib.base.BaseMvcFragment
import com.dove.readandroid.R
import com.dove.readandroid.event.UserEvent
import com.dove.readandroid.ui.common.UserShell
import com.dove.readandroid.ui.model.UserBean
import com.dove.readandroid.ui.shujia.LoginActivity
import com.safframework.ext.click
import kotlinx.android.synthetic.main.fragment_me.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class MeFragment : BaseMvcFragment() {

    override fun registerEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefresh(muservent: UserEvent) {
        setValue(UserShell.getInstance().userBean)
    }


    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_me
    }

    override fun initView() {

        if (UserShell.getInstance().userBean != null) {
            setValue(UserShell.getInstance().userBean)

        }
        tvName.click {
            if (UserShell.getInstance().isLogin) {
                start(EditUserActivity::class.java)
            } else {
                start(LoginActivity::class.java)
            }
        }
        ll_setting.click {
            start(SettingActivity::class.java)
        }
    }

    private fun setValue(userBean: UserBean) {

        tvName.text = UserShell.getInstance().userBean.name

    }

}