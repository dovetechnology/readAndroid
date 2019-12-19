package com.dove.imuguang.ui.start

import android.Manifest
import android.app.Activity
import android.media.MediaPlayer
import android.os.Handler
import android.view.View
import android.widget.MediaController
import com.appbaselib.base.BaseMvcFragment
import com.appbaselib.ext.toast
import com.appbaselib.utils.GsonUtils
import com.appbaselib.utils.PreferenceUtils

import com.dove.readandroid.R
import com.dove.readandroid.ui.common.UserShell
import com.safframework.ext.click
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

import kotlinx.android.synthetic.main.fragment_spalash.*
import java.util.concurrent.TimeUnit

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/11/25 14:52
 * ===============================
 */
class SpalashFragment(var next: () -> Unit) : BaseMvcFragment() {


    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_spalash
    }

    override fun initView() {


        RxPermissions(mContext as Activity)
            .request(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .subscribe(Consumer<Boolean> {

               next()

            }, Consumer<Throwable> {
                toast("请开启相关权限")
                activity?.finish()
            })
    }


    private fun startInit() {


    //    next()

//        image.click {
//
//            mdDisposable?.dispose()
//        }

        fl.click {
            mdDisposable?.dispose()
        }
    }

    private var mdDisposable: Disposable? = null
    private fun daojishi() {

        mdDisposable = Flowable.intervalRange(1, 5, 0, 1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                Handler().postDelayed(object : Runnable {
                    override fun run() {
                        next()
                    }

                }, 500)

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