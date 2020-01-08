package com.dove.readandroid.utils


import android.view.View
import android.widget.Toast
import com.dove.readandroid.ui.App
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/** json相关 **/
fun Any.toJson() = Gson().toJson(this)

fun <I:Any,T : Any>Any.runBackground(param: I , work: (b: I) -> T, next: (b: T) -> Unit, error: (b: Any?) -> Unit) {
    Observable.just(param)
            .map {
                work(param)
            }.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (object :Observer<T>{
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: T) {
                    next(t)
                }

                override fun onError(e: Throwable) {
                    error(e.message)
                }

            })

}
