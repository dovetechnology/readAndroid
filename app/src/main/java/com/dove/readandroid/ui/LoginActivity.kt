package com.dove.readandroid.ui.shujia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.utils.PreferenceUtils
import com.dove.readandroid.R
import com.dove.readandroid.event.UserEvent
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.common.Constants
import com.dove.readandroid.ui.common.UserShell
import com.dove.readandroid.ui.me.WangjiPassActivity
import com.jakewharton.rxbinding2.widget.RxTextView
import com.safframework.ext.click
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function4
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.tv_mima
import kotlinx.android.synthetic.main.activity_login.tv_name
import kotlinx.android.synthetic.main.title_bar.*
import org.greenrobot.eventbus.EventBus

class LoginActivity : BaseMvcActivity() {
    override fun initView(mSavedInstanceState: Bundle?) {

        var a = RxTextView.textChanges(tv_name)
        var b = RxTextView.textChanges(tv_mima)
        Observable.combineLatest(a, b, object : BiFunction<CharSequence, CharSequence, Boolean> {
            override fun apply(t1: CharSequence, t2: CharSequence): Boolean {
                return t1.isNotEmpty() && t2.isNotEmpty()
            }
        }
        ).subscribe {
            tv_denglu.isEnabled = it
        }
        tv_wangji.click {
        }
        titleBar.setRightTitle("注册账号", this).click {
            start(RegisterActivity::class.java)
        }
        tv_denglu.click {
            http().mApiService.login(tv_name.text.toString(), tv_mima.text.toString())
                .get3(isShowDialog = true) {
                    PreferenceUtils.saveObjectAsGson(mContext, Constants.USER, it?.user)
                    PreferenceUtils.setPrefString(mContext, Constants.TOKEN, it?.token)
                    UserShell.getInstance().userBean = it?.user
                    UserShell.getInstance().token = it?.token
                    EventBus.getDefault().post(UserEvent())
                    start(MainActivity::class.java)
                    finish()
                }
        }
        tv_wangji.click {
            start(WangjiPassActivity::class.java)
        }
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_login
    }


}
