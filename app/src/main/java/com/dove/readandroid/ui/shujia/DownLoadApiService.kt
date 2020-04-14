package com.dove.readandroid.ui.shujia

import com.appbaselib.network.ResponseBean
import com.dove.readandroid.ui.base.PagingBean
import com.dove.readandroid.ui.model.*
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * ===============================
 * 描    述：
 * 作    者：pjw
 * 创建日期：2019/12/18 15:01
 * ===============================
 */
interface DownLoadApiService {

    @GET
    @Streaming
    fun download(@Url url: String): Observable<ResponseBody>


}