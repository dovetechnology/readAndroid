package com.dove.readandroid.ui

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
    ): Observable<ResponseBean<UserData>>

    @FormUrlEncoded
    @POST("read/novel/user/modify")
    fun xiugai(
        @Field("username") username: String,
        @Field("mail") mail: String
    ): Observable<ResponseBean<UserData>>

    @GET("read/novel/classify")
    fun tag(): Observable<ResponseBean<List<Fenlei>>>

    @GET("read/novel/classify/detail")
    fun jingxuan(@Query("url") string: String): Observable<ResponseBean<List<Top>>>

    @GET("read/novel/top")
    fun top(): Observable<ResponseBean<List<Top>>>

    @GET("read/novel/home")
    fun home(): Observable<ResponseBean<HomeData>>

    @GET("read/novel/search")
    fun search(@Query("key") string: String): Observable<ResponseBean<Any>>

    @GET("read/novel/other/ad/list")
    fun ad(@Query("location") string: String): Observable<ResponseBean<AdDataWrapper>>

    @GET("read/novel/user/collect/list")
    fun shujiaList(): Observable<ResponseBean<List<Book>>>

    @GET("read/novel/other/act/list")
    fun huodong(
        @Query("page") page: Int, //页码号
        @Query("size") size: Int
    ): Observable<ResponseBean<PagingBean<Huodong>>>

    @FormUrlEncoded
    @POST("read/novel/user/collect")
    fun addShujia(
        @Field("name") name: String, @Field("author") author: String,
        @Field("title") title: String
    ): Observable<ResponseBean<Any>>

    @GET("read/novel/open")
    fun open(@Query("bookUrl") bookUrl: String): Observable<ResponseBean<BookWrap>>

    @GET("read/novel/open")
    fun openChap(@Query("bookUrl") bookUrl: String, @Query("chapterUrl") chapterUrl: String): Observable<ResponseBean<BookWrap>>
}