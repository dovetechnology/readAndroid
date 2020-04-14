package com.dove.readandroid.ui.me

import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.View
import androidx.annotation.MainThread
import com.appbaselib.base.BaseMvcFragment
import com.appbaselib.ext.toast
import com.appbaselib.utils.DialogUtils
import com.appbaselib.utils.LogUtils
import com.appbaselib.utils.PreferenceUtils
import com.dove.readandroid.R
import com.dove.readandroid.event.AppDataEvent
import com.dove.readandroid.event.UserEvent
import com.dove.readandroid.network.SimpleDownloadListener
import com.dove.readandroid.network.get3
import com.dove.readandroid.network.http
import com.dove.readandroid.ui.common.Constants
import com.dove.readandroid.ui.common.UserShell
import com.dove.readandroid.ui.model.AppData
import com.dove.readandroid.ui.model.UserBean
import com.dove.readandroid.ui.shujia.LoginActivity
import com.dove.readandroid.utils.FileUtlis
import com.safframework.ext.click
import com.safframework.ext.getAppVersionCode
import com.safframework.ext.installApk
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_me.*
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/16 15:43
 * ===============================
 */
class MeFragment : BaseMvcFragment() {

    var appData: AppData? = null
    override fun registerEventBus(): Boolean {
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefresh(muservent: UserEvent) {
        setValue(UserShell.getInstance().userBean)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppdataRefresh(muservent: AppData) {
        appData = muservent
        setTips()
    }

    override fun getContentViewLayoutID(): Int {
        return R.layout.fragment_me
    }

    override fun initView() {

        appData =
            PreferenceUtils.getObjectFromGson(mContext, Constants.APPDATA, AppData::class.java)
        if (UserShell.getInstance().userBean != null) {
            setValue(UserShell.getInstance().userBean)

        }
        ly_person.click {
            if (UserShell.getInstance().isLogin) {
                start(EditUserActivity::class.java)
            } else {
                start(LoginActivity::class.java)
            }
        }
        ll_setting.click {
            start(SettingActivity::class.java)
            //  start(WangjiPassActivity::class.java)
        }
        ll_lishi.click {
            start(ReadHsitoryActivity::class.java)
        }
        ll_fankui.click {
            if (UserShell.getInstance().isLogin)
                start(YijianFankuiActivity::class.java)
            else
                start(LoginActivity::class.java)
        }
        ll_share.click {
            appData?.let {
                var clip = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clip.text = it.appShare
                toast("分享地址已复制到剪贴板")

            }
        }
        ll_update.click {


            http().mApiService.appData("2", mContext.getAppVersionCode())
                .get3(isShowDialog = true) {
                    PreferenceUtils.saveObjectAsGson(mContext, Constants.APPDATA, it)
                    //更新 mefragment 的小红点
                    it?.let {
                        appData=it
                        PreferenceUtils.saveObjectAsGson(mContext, Constants.APPDATA, it)

                        if (it.refreshWebsite.isNullOrEmpty()) {
                            toast("已是最新版本")
                        } else {
                            DialogUtils.getDefaultDialog(mContext, "发现新版本，需要更新吗", title = "提") {
                                toast("后台更新中")
                                updateApp(appData!!.refreshWebsite)
                            }.show()
                        }
                    }
                }


        }
        setTips()
    }

    fun setTips() {

        appData?.let {
            if (!it.refreshWebsite.isNullOrEmpty()) {
                iv_tishi.visibility = View.VISIBLE
            }
        }
    }

    private fun setValue(userBean: UserBean?) {

        tvName.text = userBean?.name ?: "登录"

    }

    private fun updateApp(url: String) {
        var mFile = File(Constants.PATH_DOWNLOAD_FILES_DIR + "caihong_new.apk")
        http().downLoadService.download(url)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .doOnNext { it ->
                FileUtlis.writeFile2(it.byteStream(), it.contentLength(), mFile, object :
                    SimpleDownloadListener {
                    override fun onProgress(currentLength: Int) {
                        LogUtils.i("进度--->" + currentLength)
                    }
                })
            }
            .doOnError {
                Log.d("更新文件", "下载失败")
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ResponseBody> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: ResponseBody) {
                    Log.d("更新文件", "下载完成")
                    //安装文件
                    iv_tishi.visibility = View.GONE
                    mContext.installApk(mFile)
                }

                override fun onError(e: Throwable) {
                    toast(e.message)
                }
            })


    }

}