package com.dove.readandroid.ui.me

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.ext.toast
import com.appbaselib.utils.PreferenceUtils
import com.dove.readandroid.R
import com.dove.readandroid.event.UserEvent
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.common.Constants
import com.dove.readandroid.ui.common.UserShell
import com.safframework.ext.click
import kotlinx.android.synthetic.main.activity_edit_user.*
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.android.synthetic.main.title_bar.*
import org.greenrobot.eventbus.EventBus

class EditUserActivity : BaseMvcActivity() {
    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_edit_user
    }

    override fun initView(mSavedInstanceState: Bundle?) {
        titleBar.setTitle("编辑个人信息")
        btn_save.click {
            http().mApiService.xiugai(tv_name.text.toString(),tv_email.text.toString())
                .get3 {
                    UserShell.getInstance().userBean.username=tv_name.text.toString()
                    UserShell.getInstance().userBean.mail=tv_email.text.toString()
                    PreferenceUtils.saveObjectAsGson(mContext,Constants.USER,UserShell.getInstance().userBean)
                    EventBus.getDefault().post(UserEvent())
                    toast("修改成功")
                    finish()
                }
        }
    }


}
