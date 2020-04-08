package com.dove.readandroid.ui.shujia

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
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
import com.dove.readandroid.event.ButtonClick
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
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import android.content.Intent
import android.widget.Toast
import com.appbaselib.utils.DateUtils
import com.dove.readandroid.ui.huodong.HuodongFragment2


class MainActivity : BaseMvcActivity() {
    lateinit var mshujia: ShujiaFragment
    lateinit var huodongfragment: HuodongFragment
    lateinit var mefragment: MeFragment
    lateinit var shuchengfragment: ShuchengFragment
    lateinit var navigator: Navigator

    override fun getContentViewLayoutID(): Int {
        return com.dove.readandroid.R.layout.activity_main
    }
    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
    }

    var id = 0;
    override fun initView(mSavedInstanceState: Bundle?) {
        navigator = Navigator(supportFragmentManager, com.dove.readandroid.R.id.content)
        mshujia = ShujiaFragment()
        huodongfragment = HuodongFragment()
        mefragment = MeFragment()
        shuchengfragment = ShuchengFragment()
      //  navigator.showFragment(shuchengfragment)//提前请求网络
        navigator.showFragment(mshujia)
        navigation.setOnNavigationItemSelectedListener {
            if (id == it.itemId) {
                EventBus.getDefault().post(ButtonClick())
            }
            when (it.itemId) {
                com.dove.readandroid.R.id.main_shujia -> navigator.showFragment(mshujia)
                com.dove.readandroid.R.id.main_shucheng -> navigator.showFragment(shuchengfragment)

                com.dove.readandroid.R.id.main_huodong -> navigator.showFragment(huodongfragment)

                com.dove.readandroid.R.id.main_my -> {
//                    if (!UserShell.getInstance().isLogin) {
//                        start(LoginActivity::class.java)
//                        return@setOnNavigationItemSelectedListener true
//                    }
                    navigator.showFragment(mefragment)
                }

            }
            id = it.itemId
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

                if (it != null && it.list.isNotEmpty()) {
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
        http().mApiService.appData("2", getAppVersionCode()).get3 {
            PreferenceUtils.saveObjectAsGson(mContext, Constants.APPDATA, it)
            //更新 mefragment 的小红点
            EventBus.getDefault().post(AppDataEvent(it))
        }
    }

    private fun getUrl() {
      //  var url = "https://raw.githubusercontent.com/dovetechnology/address/master/readingUrl"
        var url="https://raw.githubusercontent.com/wawowa/test1/master/1.txt"
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
                if (TextUtils.isEmpty(url)) {
                    url = "http://www." + AESUtilFinal.encrypt(
                        "abce0123456789ef",
                        DateUtils.getCurrentTimeYmd()
                    ).substring(0, 10) + ".com/"
                }
                PreferenceUtils.setPrefString(mContext, Constants.URL, url)

            }, err = {

            })

    }

//    //按两次返回到桌面
//    private var exitTime: Long = 0
//
//    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK
//            && event.getAction() == KeyEvent.ACTION_DOWN
//        ) {
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                toast("再按一下返回到桌面")
//                exitTime = System.currentTimeMillis();
//            } else {
//                var i = Intent(Intent.ACTION_MAIN);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.addCategory(Intent.CATEGORY_HOME);
//                startActivity(i);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//
//    }

}
