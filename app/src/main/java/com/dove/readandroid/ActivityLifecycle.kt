package com.appbaselib.app

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.appbaselib.utils.PreferenceUtils
import com.dove.readandroid.ui.OpenTypeHandler
import com.dove.readandroid.ui.common.Constants
import com.dove.readandroid.ui.model.AdData

/*
 */
class ActivityLifecycle(private val mApplication: Application) :
    Application.ActivityLifecycleCallbacks {

    private val mAppManager: AppManager

    private val mFragmentLifecycles: List<androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks>? =
        null

    init {
        mAppManager = AppManager.getInstance()
        mAppManager.setApplication(mApplication)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        //如果intent包含了此字段,并且为true说明不加入到list
        // 默认为false,如果不需要管理(比如不需要在退出所有activity(killAll)时，退出此activity就在intent加此字段为true)
        var isNotAdd = false
        if (activity.intent != null) {
            isNotAdd = activity.intent.getBooleanExtra(AppManager.IS_NOT_ADD_ACTIVITY_LIST, false)
        }
        if (!isNotAdd) {
            mAppManager.addActivity(activity)
        }

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

        //广告管理
        var isshow =
            PreferenceUtils.getPrefBoolean(mAppManager.currentActivity, Constants.IS_SHOW_AD, false)
        if (isshow) {
            var guanggao = PreferenceUtils.getObjectFromGson(
                mAppManager.currentActivity,
                Constants.AD,
                AdData::class.java
            )
            guanggao?.let {
                OpenTypeHandler(it, activity).handle()
            }

            PreferenceUtils.setPrefBoolean(mAppManager.currentActivity, Constants.IS_SHOW_AD, false)
        } else {
            //其他事情

        }
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        mAppManager.removeActivity(activity)

    }

}
