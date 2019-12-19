package com.appbaselib.network


import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.appbaselib.app.AppManager
import com.appbaselib.ext.toast
import com.dove.readandroid.ui.common.UserShell
import com.dove.readandroid.ui.shujia.LoginActivity

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.HttpException

/**
 * 2019 5.6 tangming  只获取结果 T
 * 通用订阅者,用于统一处理回调
 */
abstract class MySubscriber<T>(context: Context? = null, mMessage: String? = "请稍后……", title: String? = null) : Observer<T> {

    private var mContext: Context? = null
    private var mDisposable: Disposable? = null
    private var mProgressDialog: ProgressDialog? = null
    private var title: String? = null
    private var message: String? = null

    /**
     * @param context  context
     * @param mMessage dialog message
     */
    init {
        this.mContext = context
        this.message = mMessage
        this.title = title
    }

    override fun onComplete() {}

    override fun onSubscribe(b: Disposable) {
        mDisposable = b

        if (mContext != null) {
            mProgressDialog = ProgressDialog(mContext)

            if (!TextUtils.isEmpty(title))
                mProgressDialog!!.setTitle(title)
            if (!TextUtils.isEmpty(message))
                mProgressDialog!!.setMessage(message)

            mProgressDialog!!.setCancelable(true)
            mProgressDialog!!.setCanceledOnTouchOutside(false)
            //点击取消的时候取消订阅  也就是取消网络请求
            mProgressDialog!!.setOnCancelListener { mDisposable!!.dispose() }
            mProgressDialog!!.show()
        }
    }

    override fun onNext(mBaseModel: T) {
        if (mContext != null)
            mProgressDialog!!.dismiss()

        onSucess(mBaseModel)

    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        if (mContext != null)
            mProgressDialog!!.dismiss()

        e?.printStackTrace()
        if (e is ApiException) run { doServerErrorCode(e) }
        else if (e is HttpException) run {
            onFail("数据加载失败ヽ(≧Д≦)ノ")
        } else {
            if (e == null) {
                onFail("网络响应失败，请重试")
                return
            }
            var message: String? = null
            val exceptionMsg = e.toString()
            if (exceptionMsg.contains("IOException") || exceptionMsg.contains("Socket closed")) {
                message = ""
            } else if (exceptionMsg.contains("JSON")) {
                    message = "JSON数据解析失败"
            } else if (exceptionMsg.contains("UnknownHost")) {
                message = "网络连接失败，请重试"
            } else if (exceptionMsg.contains("SocketTimeoutException")) {
                message = "网络连接超时，请重试"
            } else if (exceptionMsg.contains("Socket")
                    || exceptionMsg.contains("Connect")
                    || exceptionMsg.contains("SSL")
                    || exceptionMsg.contains("ConcurrentModification")
                    || exceptionMsg.contains("IndexOutOfBounds")
                    || exceptionMsg.contains("ProtocolException")
                    || exceptionMsg.contains("ERR_INTERNET")
                    || exceptionMsg.contains("HostException")) {
                message = "网络连接失败，请重试"
            } else {
                message = "数据加载失败"//exceptionMsg;
            }
            onFail(message)
        }
    }

    /**
     * 处理服务器定义的错误
     *
     * @param e
     */
    private fun doServerErrorCode(e: ApiException) {
        when (e.code) {
            -2//处理被其他设备挤掉线 token失效
            -> {
                login()
            }
            -3//没有登录
            -> {
                login()
            }
            else -> {
                onFail(e.message)
            }
        }
    }

    protected abstract fun onSucess(t: T)

    //默认实现
    protected open fun onFail(message: String?) {
        if (message != null) {
            toast(message)
        }
    }

    /**
     * 重新登陆
     */
    private fun login() {
        UserShell.getInstance().exitLogin()//清除用户信息
        //关闭当前页并跳转到登录
        AppManager.getInstance().currentActivity.startActivity(
                Intent(AppManager.getInstance().currentActivity, LoginActivity::class.java))
    }

}

