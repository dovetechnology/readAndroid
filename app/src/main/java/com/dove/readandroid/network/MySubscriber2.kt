package com.appbaselib.network


import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.appbaselib.app.AppManager
import com.appbaselib.ext.toast
import com.appbaselib.utils.NetWorkUtils
import com.dove.readandroid.ui.App
import com.dove.readandroid.ui.common.UserShell
import com.dove.readandroid.ui.shujia.LoginActivity
import com.google.gson.JsonSyntaxException
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * 2019 5.6 tangming  获取全部结果
 * 通用订阅者,用于统一处理回调
 */
abstract class MySubscriber2<T>(context: Context? = null, mMessage: String? = "请稍后……", title: String? = null) : Observer<ResponseBean<T>> {
    constructor(mContextm: Context) : this() {
        mContext = mContextm
    }


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

    override fun onNext(mBaseModel: ResponseBean<T>) {
        try {

            if (mContext != null)
            mProgressDialog?.dismiss()

        onSucess(mBaseModel.data)
        }catch (e:Exception )
        {
            return error("加载失败");
        }
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        if (mContext != null)
            mProgressDialog!!.dismiss()

        if (e is ApiException) {
            doServerErrorCode(e)
        } else if (!NetWorkUtils.isNetworkConnected(App.instance)) { //判断网络
            onFail("网络不可用")
        } else if (e is JsonSyntaxException) {  //其余不知名错误
            onFail("数据解析异常")
        }else{
            onFail(e.message) //其他异常
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
                // toast("您的设备在其他地方登陆")
                login()
            }
            -3//没有登录
            -> {
                login()
            }
            else -> {
                if (TextUtils.isEmpty(e.message))
                    onFail("请求失败，请稍后再试...")
                else
                    onFail(e.message)
            }
        }
        onComplete()
    }

    protected abstract fun onSucess(t: T?)

    //默认实现
    protected open fun onFail(message: String?) {
        toast(message)
    }


    /**
     * 重新登陆
     */
    private fun login() {
        UserShell.getInstance().exitLoginButSavePre()//清除用户信息
        //关闭当前页并跳转到登录
        AppManager.getInstance().currentActivity.startActivity(
                Intent(AppManager.getInstance().currentActivity, LoginActivity::class.java))
    }

}

