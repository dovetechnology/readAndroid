package com.dove.readandroid.ui

import com.appbaselib.network.ResponseBean
import com.dove.readandroid.ui.base.PagingBean
import com.dove.readandroid.ui.model.Fenlei
import com.dove.readandroid.ui.model.Top
import com.dove.readandroid.ui.model.UserBean
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
interface ApiService {
    //获取真实url
    @GET
    fun getUrl(@Url url: String): Observable<ResponseBody>

    @FormUrlEncoded
    @POST("read/novel/user/register")
    fun register(@Field("username") username: String, @Field("password") pass: String, @Field("mail") mail: String): Observable<ResponseBean<UserBean>>

    @FormUrlEncoded
    @POST("read/novel/user/login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Observable<ResponseBean<UserBean>>


    @GET("read/novel/classify")
    fun tag(): Observable<ResponseBean<List<Fenlei>>>

    @GET("read/novel/top")
    fun top(): Observable<ResponseBean<List<Top>>>
}