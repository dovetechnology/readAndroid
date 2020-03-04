package com.dove.readandroid.ui.shujia

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import com.appbaselib.base.BaseMvcActivity
import com.appbaselib.base.Navigator
import com.appbaselib.common.ImageLoader
import com.appbaselib.ext.toast
import com.appbaselib.utils.PreferenceUtils
import com.dhh.rxlife2.RxLife
import com.dove.rea.ShuchengFragment
import com.dove.readandroid.BuildConfig
import com.dove.readandroid.R
import com.dove.readandroid.event.AppDataEvent
import com.dove.readandroid.network.get2
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.common.AESUtilFinal
import com.dove.readandroid.ui.common.CommonParamUtil
import com.dove.readandroid.ui.common.Constants
import com.dove.readandroid.ui.huodong.HuodongFragment
import com.dove.readandroid.ui.me.MeFragment
import com.dove.readandroid.ui.model.AdData
import com.dove.readandroid.utils.FileUtlis
import com.safframework.ext.getAppVersionCode
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_fenlei_detail.*
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import java.io.File

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
//埋点
        http().mApiService.start("2", CommonParamUtil.getUUID(this))
            .get3 {

        }
        getUrl()
        getAppData()
        getAd()
    }

    private fun getAd() {
        http().mApiService.ad("1")
            .get3 {

                if (it!=null&&it.list.get(0)!=null) {
                    PreferenceUtils.saveObjectAsGson(this@MainActivity, Constants.AD, it?.list[0])
                }

            }
    }
    private fun cacheAd(guanggao: AdData, cacheKey: String) {
        if (!TextUtils.isEmpty(guanggao.imgUrl)) {
            //预加载广告
            ImageLoader.load(this@MainActivity, guanggao.imgUrl, ImageView(this@MainActivity))
            PreferenceUtils.saveObjectAsGson(this@MainActivity, cacheKey, guanggao)
        } else {

            val mFile = File(Constants.PATH_CACHE_DIR + System.currentTimeMillis() + ".mp4")
            http().mApiService.download(guanggao.videoUrl)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map { mResponseBody -> mResponseBody.byteStream() }
                .observeOn(Schedulers.computation())
                .doOnNext { mInputStream -> FileUtlis.writeFile(mInputStream, mFile) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("down", "视频下载完成")
                    guanggao.videoUrl = mFile.absolutePath
                    PreferenceUtils.saveObjectAsGson(this@MainActivity, cacheKey, guanggao)
                }

        }
    }
    private fun getAppData() {
        http().mApiService.appData("2",getAppVersionCode()).get3 {
            PreferenceUtils.saveObjectAsGson(mContext,Constants.APPDATA,it)
            //更新 mefragment 的小红点
            EventBus.getDefault().post(AppDataEvent(it))
        }
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
            .compose(RxLife.with(this).bindOnDestroy())
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .get2(next = {
                var url = AESUtilFinal.decrypt("abce0123456789ef", it.string().trim())
                PreferenceUtils.setPrefString(mContext, Constants.URL, url)
            }, err = {

            })

    }

}
