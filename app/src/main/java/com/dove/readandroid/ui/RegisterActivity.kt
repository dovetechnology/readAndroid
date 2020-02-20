package com.dove.readandroid.ui.shujia

import android.annotation.SuppressLint
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
import com.jakewharton.rxbinding2.widget.RxTextView
import com.safframework.ext.click
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function4
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus

class RegisterActivity : BaseMvcActivity() {
    override fun initView(mSavedInstanceState: Bundle?) {
        var a = RxTextView.textChanges(tv_name)
        var b = RxTextView.textChanges(tv_mima)
        Observable.combineLatest(a,
            b,
            RxTextView.textChanges(tv_suremima),
            RxTextView.textChanges(tv_email),
            object : Function4<CharSequence, CharSequence, CharSequence, CharSequence, Boolean> {
                override fun apply(
                    t1: CharSequence,
                    t2: CharSequence,
                    t3: CharSequence,
                    t4: CharSequence
                ): Boolean {
                    return t1.toString().isNotEmpty() && t2.toString().isNotEmpty() && t2.toString().equals(
                        t3.toString()
                    )
                }

            }
        ).subscribe {
            tv_register.isEnabled = it
        }
        tv_register.click {
            http().mApiService.register(
                tv_name.text.toString(),
                tv_mima.text.toString(),
                tv_email.text.toString()
            )
                .get3 {

                    http().mApiService.login(tv_name.text.toString(), tv_mima.text.toString())
                        .get3 {
                            PreferenceUtils.saveObjectAsGson(mContext, Constants.USER, it)
                            PreferenceUtils.setPrefString(mContext, Constants.TOKEN, it?.token)
                            UserShell.getInstance().userBean = it?.user
                            UserShell.getInstance().token = it?.token
                            EventBus.getDefault().post(UserEvent())
                            start(MainActivity::class.java)
                            finish()
                        }
                }
        }

    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_register
    }


}
