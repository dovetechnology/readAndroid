package com.dove.imuguang.ui.start

import android.Manifest
import android.app.Activity
import android.media.MediaPlayer
import android.os.Handler
import android.view.View
import android.widget.MediaController
import com.appbaselib.base.BaseMvcFragment
import com.appbaselib.common.load
import com.appbaselib.ext.toast
import com.appbaselib.utils.GsonUtils
import com.appbaselib.utils.PreferenceUtils

import com.dove.readandroid.R
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.OpenTypeHandler
import com.dove.readandroid.ui.common.Constants
import com.dove.readandroid.ui.common.UserShell
import com.dove.readandroid.ui.model.AdData
import com.safframework.ext.click
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_play_video.*
import kotlinx.android.synthetic.main.fragment_spalash.*
import kotlinx.android.synthetic.main.fragment_spalash.content
import kotlinx.android.synthetic.main.video_layout_ad.view.*
import java.util.concurrent.TimeUnit

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/11/25 14:52
 * ===============================
 */
class SpalashFragment(var next: () -> Unit) : BaseMvcFragment() {

    var ad: AdData? = null

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_spalash
    }

    override fun initView() {

        ad = PreferenceUtils.getObjectFromGson(mContext, Constants.AD, AdData::class.java)

        RxPermissions(mContext as Activity)
            .request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .subscribe(Consumer<Boolean> {
                startInit()
            }, Consumer<Throwable> {
                toast("请开启相关权限")
                activity?.finish()
            })
    }


    private fun startInit() {

        if (ad != null) {

            if ("1".equals(ad!!.screen)) {
                ratiolayout.setRatio(0f)
            } else {
                ratiolayout.setRatio(0.77f)
            }
            if (ad!!.imgUrl.isNotEmpty()) {
                //图片
                fl.visibility = View.VISIBLE
                video_view.visibility = View.GONE
                image.visibility = View.VISIBLE

                image.load(ad?.imgUrl)
            } else {
                //视频
                fl.visibility = View.VISIBLE
                video_view.visibility = View.VISIBLE
                image.visibility = View.GONE

                //初始化视频
                GSYVideoManager.instance().setPlayerInitSuccessListener { player, model ->
                    GSYVideoManager.instance().isNeedMute = true            //设置静音
                }
                GSYVideoOptionBuilder().apply {
                    setUrl(ad?.videoUrl)
                        .setCacheWithPlay(true)
                        .build(video_view)
                }
                video_view.startPlayLogic()
                video_view.isEnabled = false
            }
            daojishi()
        } else {
            //没有广告直接回调
            next()

        }

        content.click {
            ad?.let {

                //                var map = mutableMapOf<String, String>()
//                var list = arrayListOf<AdMiandianModel>()
//                list.add(AdMiandianModel(guanggao?.id, UserShell.getInstance().userBean?.id))
//                map.put("adBuryJson", GsonUtils.GsonString(list))
//                http().userApiService.maidian(map)
//                    .get3(next = {}, err = {
//
//                    })
                //通过 ActivityLifecycle去管理广告
                PreferenceUtils.setPrefBoolean(mContext, Constants.IS_SHOW_AD, true)
            }
            video_view.onVideoPause()
            mdDisposable?.dispose()
        }

        fl.click {
            mdDisposable?.dispose()
        }
    }

    private var mdDisposable: Disposable? = null
    private fun daojishi() {

        mdDisposable = Flowable.intervalRange(1, 5, 0, 1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                next()
            }
            .doOnCancel {
                Handler().postDelayed(object : Runnable {
                    override fun run() {
                        next()
                    }

                }, 1000)
            }
            .subscribe {
                tv_time.setText("跳过 ${5 - it}")
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        mdDisposable?.dispose()
    }


    override fun getLoadingTargetView(): View? {
        return null
    }
}