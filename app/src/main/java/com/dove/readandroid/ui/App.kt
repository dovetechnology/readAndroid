package com.dove.readandroid.ui

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

import android.util.Log
import androidx.room.Room

import com.alibaba.android.arouter.launcher.ARouter
import com.appbaselib.app.ActivityLifecycle
import com.appbaselib.app.BaseApplication
import com.appbaselib.app.CrashHandler
import com.appbaselib.ext.toast
import com.appbaselib.utils.LogUtils
import com.appbaselib.utils.PreferenceUtils
import com.dove.readandroid.BuildConfig
import com.tencent.bugly.Bugly
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import com.umeng.message.UmengMessageHandler
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.entity.UMessage
import com.umeng.socialize.PlatformConfig
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

/**
 * ===============================
 * 描    述：app
 * 作    者：pjw
 * 创建日期：2019/3/18 15:42
 * ===============================
 */
class App : BaseApplication() {
    lateinit var mActivityLifecycle: ActivityLifecycle


    override fun onCreate() {
        super.onCreate()
        //activity 生命周期监听
       instance=this
        mActivityLifecycle = ActivityLifecycle(this)
        registerActivityLifecycleCallbacks(mActivityLifecycle)
        initArouter()
        UMConfigure.init(this, "xxxxx", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "0f53054413b6829bacde7587384cb0cf")
        //获取消息推送代理示例
        val mPushAgent = PushAgent.getInstance(this)
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(object : IUmengRegisterCallback {
            override fun onSuccess(deviceToken: String) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i("tag", "友盟推送注册成功：deviceToken：-------->  $deviceToken")

            }

            override fun onFailure(s: String, s1: String) {
                Log.e("tag", "友盟推送注册失败：-------->  s:$s,s1:$s1")
            }
        })

        mPushAgent.notificationClickHandler = object : UmengNotificationClickHandler() {
            override fun dealWithCustomAction(mContext: Context?, mUMessage: UMessage?) {
                LogUtils.d(mUMessage!!.toString())
            }

            override fun launchApp(p0: Context?, p1: UMessage?) {
                super.launchApp(p0, p1)
                var mJSONObject = JSONObject(p1?.custom)

            }

        }
        mPushAgent.messageHandler = object : UmengMessageHandler() {

            override fun dealWithCustomMessage(mContext: Context?, mUMessage: UMessage?) {
                LogUtils.d(mUMessage!!.toString())

            }
        }
        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         */
        UMConfigure.setLogEnabled(true)
        if (!BuildConfig.DEBUG) {
            CrashHandler.getInstance().init(this)
        }
        //友盟相关平台配置。注意友盟官方新文档中没有这项配置，但是如果不配置会吊不起来相关平台的授权界面
        PlatformConfig.setWeixin("wxa0bbb8cdfe8c5b82", "AppSecret:a102e31c8341c37d8b4b65a752cbb759")//微信APPID和AppSecret
        PlatformConfig.setQQZone("1108323392", "S9PENLfddHZJMQ2C")//QQAPPID和AppSecret
        PlatformConfig.setSinaWeibo("你的微博APPID", "你的微博APPSecret", "微博的后台配置回调地址")//微博

        Bugly.init(applicationContext, "xxxxx", BuildConfig.DEBUG)
        //初始化数据库
    }

    private fun initArouter() {
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog()     // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this) // 尽可能早，推荐在Application中初始化
    }

    companion object {
        lateinit var instance: Context
    }

}
