package com.dove.readandroid.ui.shujia

import android.os.Bundle
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.base.Navigator
import com.appbaselib.ext.toast
import com.appbaselib.rxlife.RxLife
import com.appbaselib.utils.PreferenceUtils
import com.dove.rea.ShuchengFragment
import com.dove.readandroid.BuildConfig
import com.dove.readandroid.R
import com.dove.readandroid.network.get2
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.common.AESUtilFinal
import com.dove.readandroid.ui.common.Constants
import com.dove.readandroid.ui.huodong.HuodongFragment
import com.dove.readandroid.ui.me.MeFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseMvcActivity() {
    lateinit var mshujia: ShujiaFragment
    lateinit var huodongfragment: HuodongFragment
    lateinit var mefragment: MeFragment
    lateinit var shuchengfragment: ShuchengFragment
    lateinit var navigator: Navigator

    override fun getContentViewLayoutID(): Int {
        return R.layout.activity_main
    }

    override fun initView(mSavedInstanceState: Bundle?) {
        navigator = Navigator(supportFragmentManager, R.id.content)
        mshujia = ShujiaFragment()
        huodongfragment = HuodongFragment()
        mefragment = MeFragment()
        shuchengfragment = ShuchengFragment()
        navigator.showFragment(mshujia)
        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.main_shujia -> navigator.showFragment(mshujia)
                R.id.main_shucheng -> navigator.showFragment(shuchengfragment)

                R.id.main_huodong -> navigator.showFragment(huodongfragment)

                R.id.main_my -> {
//                    if (!UserShell.getInstance().isLogin) {
//                        start(LoginActivity::class.java)
//                        return@setOnNavigationItemSelectedListener true
//                    }
                    navigator.showFragment(mefragment)
                }

            }
            return@setOnNavigationItemSelectedListener true
        }
        navigation.setTextSize(12f)
        navigation.setTextVisibility(true)
        //   navigation.enableAnimation(true)
        navigation.enableItemShiftingMode(false)
        navigation.enableShiftingMode(false)

        getUrl()
    }

    private fun getUrl() {
        var url = "https://raw.githubusercontent.com/dovetechnology/address/master/readingUrl"

//        if (BuildConfig.BASE_URL.contains("imuguang")) {
//            if (BuildConfig.DEBUG) {
//                url = "${Constants.IMAGE}rluri-test.txt?"
//            } else {
//                url = "${Constants.IMAGE}rluri.txt?"
//            }
//        } else {
//            url = "${Constants.IMAGE}rluri.txt?"
//        }

        http().mApiService.getUrl(url)
            .compose(RxLife.with(this).bindToLifecycle())
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .get2(next = {
                var url = AESUtilFinal.decrypt("abce0123456789ef", it.string().trim())
                PreferenceUtils.setPrefString(mContext, Constants.URL, url)
            }, err = {
    
            })

    }

}
