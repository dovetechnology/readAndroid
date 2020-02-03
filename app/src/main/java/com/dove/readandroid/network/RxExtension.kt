package com.dove.readandroid.network
import androidx.lifecycle.LifecycleOwner
import android.content.Context
import com.appbaselib.app.AppManager
import com.appbaselib.network.*
import com.dove.readandroid.ui.RetrofitHelper
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Function
import java.lang.Exception

//不带绑定生命周期 需要手动调用rxcompose.handleResult 方法
fun <T> Observable<T>.get2(context: Context? = null, message: String? = "请稍候", title: String? = "", next: (T) -> Unit, err: (mS: String?) -> Unit) {

    this.subscribe(object : MySubscriber<T>(context, message, title) {
        override fun onSucess(t: T) {
            next(t)
        }

        override fun onFail(message: String?) {
            err(message)
        }
    })
}

fun <T> Observable<T>.get2(context: Context? = null, message: String? = "请稍候", title: String? = null, next: (T) -> Unit) {

    this.subscribe(object : MySubscriber<T>(context, message, title) {
        override fun onSucess(t: T) {
            next(t)
        }

    })

}
//------------------------------------------------------------只返回T -------------------------------------------------
//绑定生命周期 不覆写onFail 默认弹出toast

fun <T> Observable<ResponseBean<T>>.get(context: Context = AppManager.getInstance().currentActivity, isShowDialog: Boolean = false, message: String? = "请稍候", title: String? = null, next: (T) -> Unit) {

    this.compose(RxHttpUtil.handleResult<T>(context as LifecycleOwner)).subscribe(object : MySubscriber<T>(if (isShowDialog) context else null, message, title) {
        override fun onSucess(t: T) {
            next(t)
        }

    })

}

//绑定生命周期 复写onFail
fun <T> Observable<ResponseBean<T>>.get(context: Context = AppManager.getInstance().currentActivity, isShowDialog: Boolean = false, message: String? = "请稍候", title: String? = "", next: (T?) -> Unit, err: (mS: String?) -> Unit) {

    this.compose(RxHttpUtil.handleResult<T>(context as LifecycleOwner)).subscribe(object : MySubscriber<T?>(if (isShowDialog) context else null, message, title) {
        override fun onSucess(t: T?) {
            next(t)
        }

        override fun onFail(message: String?) {
            err(message)
        }
    })
}
//-------------------------------------------------------------------------------------------------------------

//----------------------------------------------------返回 整个网络结果对象---------------------------------------------------------
fun <T> Observable<ResponseBean<T>>.get3(context: Context = AppManager.getInstance().currentActivity, isShowDialog: Boolean = false, message: String? = "请稍候", title: String? = null, next: (T?) -> Unit) {

    this.compose(RxHttpUtil.handleResult2<T>(context as LifecycleOwner))
            .subscribe(object : MySubscriber2<T>(if (isShowDialog) context else null, message, title) {
                override fun onSucess(t: T?) {
                    next(t)
                }

            })

}

//绑定生命周期 复写onFail
fun <T> Observable<ResponseBean<T>>.get3(context: Context = AppManager.getInstance().currentActivity, isShowDialog: Boolean = false, message: String? = "请稍候", title: String? = "", next: (T?) -> Unit, err: (mS: String?) -> Unit,complete: (() -> Unit)?=null) {

    this.compose(RxHttpUtil.handleResult2<T>(context as LifecycleOwner)).subscribe(object : MySubscriber2<T>(if (isShowDialog) context else null, message, title) {
        override fun onSucess(t: T?) {
            next(t)
        }

        override fun onFail(message: String?) {
            err(message)
        }

        override fun onComplete() {
            complete?.let {
                it()
            }
        }
    })
}
//不绑定生命周期 复写onFail
fun <T> Observable<ResponseBean<T>>.get4(context: Context = AppManager.getInstance().currentActivity, isShowDialog: Boolean = false, message: String? = "请稍候", title: String? = "", next: (T?) -> Unit, err: (mS: String?) -> Unit,complete: (() -> Unit)?=null) {

    this.subscribe(object : MySubscriber2<T>(if (isShowDialog) context else null, message, title) {
        override fun onSucess(t: T?) {
            next(t)
        }

        override fun onFail(message: String?) {
            err(message)
        }
        override fun onComplete() {
            complete?.let {
                it()
            }
        }
    })
}

//-------------------------------------------------------------------------------------------------------------
//简化网络库的调用
fun Any.http(): RetrofitHelper {
    return RetrofitHelper.getInstance()
}
