package com.dove.readandroid.ui.shujia

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.View
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.utils.PreferenceUtils
import com.dove.readandroid.R
import com.dove.readandroid.event.UserEvent
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.WebViewActivity
import com.dove.readandroid.ui.common.Constants
import com.dove.readandroid.ui.common.UserShell
import com.framework.ext.clickSpan
import com.framework.ext.colorSpan
import com.jakewharton.rxbinding2.widget.RxTextView
import com.safframework.ext.click
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function4
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus
import androidx.core.content.ContextCompat
import com.dove.readandroid.ui.model.AppData
import com.framework.ext.toColorSpan

class RegisterActivity : BaseMvcActivity() {
    var appData: AppData? = null

    override fun initView(mSavedInstanceState: Bundle?) {
        appData = PreferenceUtils.getObjectFromGson(mContext, Constants.APPDATA, AppData::class.java)

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

        var spannableString = SpannableString(tv_yueduxuzhi.text.toString());

        tv_yueduxuzhi.text.toString()
            .toColorSpan(12..16, ContextCompat.getColor(mContext, R.color.colorAccent))
        tv_yueduxuzhi.text.toString()
            .toColorSpan(19..23, ContextCompat.getColor(mContext, R.color.colorAccent))

        tv_yueduxuzhi.movementMethod = LinkMovementMethod.getInstance()
        tv_yueduxuzhi.highlightColor = Color.TRANSPARENT  // remove click bg color

        tv_yueduxuzhi.text = SpannableString(tv_yueduxuzhi.text)
            .apply {
                setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorAccent)),
                    12,
                    16,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorAccent)),
                    19,
                    23,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                              WebViewActivity.instance(appData?.userAgreement,mContext)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = ContextCompat.getColor(mContext, R.color.colorAccent)
                        ds.isUnderlineText = true
                    }
                }
                setSpan(clickableSpan, 12, 16, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                val clickableSpan2 = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        WebViewActivity.instance(appData?.privacyPolicies,mContext)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = ContextCompat.getColor(mContext, R.color.colorAccent)
                        ds.isUnderlineText = true
                    }
                }
                setSpan(clickableSpan2, 19, 23, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }




    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_register
    }


}
