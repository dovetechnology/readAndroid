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
        @Field("name") username: String,
        @Field("mail") mail: String
    ): Observable<ResponseBean<UserData>>

    @GET("read/novel/classify")
    fun tag(): Observable<ResponseBean<List<Fenlei>>>

    @GET("read/novel/classify/detail")
    fun fenleiDetail(
        @Query("id") id: String, @Query("edition") edition: String, @Query("page") page: Int, //页码号
        @Query("size") size: Int
    ): Observable<ResponseBean<PagingBean<Book>>>

    @GET("read/novel/top/new")
    fun top(
        @Query("type") id: String, @Query("edition") edition: String, @Query("page") page: Int, //页码号
        @Query("size") size: Int
    ): Observable<ResponseBean<PagingBean<Book>>>

    @GET("read/novel/top/classify")
    fun paihangTag(): Observable<ResponseBean<List<Top>>>

    @GET("read/novel/home")
    fun home(): Observable<ResponseBean<HomeData>>

    @GET("read/novel/lastupdate")
    fun lastupdate(
        @Query("edition") edition: String, @Query("page") page: Int, //页码号
        @Query("size") size: Int
    ): Observable<ResponseBean<PagingBean<Book>>>


    @GET("read/novel/search")
    fun search(
        @Query("key") string: String, @Query("page") page: Int, //页码号
        @Query("size") size: Int
    ): Observable<ResponseBean<PagingBean<Book>>>

    @GET("read/novel/other/ad/list")
    fun ad(@Query("location") string: String): Observable<ResponseBean<AdDataWrapper>>

    @GET("read/novel/user/collect/list")
    fun shujiaList(): Observable<ResponseBean<List<BookShelf>>>


    @FormUrlEncoded
    @POST("read/novel/user/del/bookshelf")
    fun removeShujia(@Field("id") id: String): Observable<ResponseBean<Any>>


    @GET("read/novel/other/act/list")
    fun huodong(
        @Query("page") page: Int, //页码号
        @Query("size") size: Int
    ): Observable<ResponseBean<PagingBean<Huodong>>>


    @FormUrlEncoded
    @POST("read/novel/open/name")
    fun openName(@Field("name") name: String, @Field("author") author: String, @Field("title") title: String): Observable<ResponseBean<BookWrap>>

    @FormUrlEncoded
    @POST("read/novel/user/collect")
    fun addShujia(
        @Field("articleId") name: String, @Field("chapterId") author: String
    ): Observable<ResponseBean<Any>>

    @GET("read/novel/open")
    fun open(@Query("bookUrl") bookUrl: String): Observable<ResponseBean<BookWrap>>

    @GET("read/novel/chapter/list")
    fun chapterList(
        @Query("bookId") bookId: String, @Query("page") page: Int, //页码号
        @Query("size") size: Int
    ): Observable<ResponseBean<ChapterWrap>>

    @GET("read/novel/open")
    fun openChap(@Query("bookUrl") bookUrl: String, @Query("chapterUrl") chapterUrl: String): Observable<ResponseBean<ChapWrap>>

    @POST("read/novel/user/find/password")
    fun findpass(@Field("username") name: String): Observable<ResponseBean<Any>>

    @GET("read/novel/source/list")
    fun source(): Observable<ResponseBean<List<Source>>>

    @FormUrlEncoded
    @POST("read/novel/change/source")
    fun sourceChange(
        @Field("name") name: String, @Field("author") author: String,
        @Field("title") title: String, @Field("id") id: String
    ): Observable<ResponseBean<Any>>

    @FormUrlEncoded
    @POST("read/novel/other/visit/bury")
    fun start(@Field("userAgent") userAgent: String, @Field("deviceId") deviceId: String): Observable<ResponseBean<Any>>

    //埋点json数据,格式:[{adId:广告ID,userId:用户ID}]
    @FormUrlEncoded
    @POST("read/novel/other/ad/bury")
    fun maidian(@Field("adBuryJson") adBuryJson: String): Observable<ResponseBean<Any>>

    @FormUrlEncoded
    @POST("read/novel/other/message")
    fun fankui(@Field("content") content: String): Observable<ResponseBean<Any>>

    @GET
    @Streaming
    fun download(@Url url: String): Observable<ResponseBody>

    @GET("read/novel/other/app/config")
    fun appData(@Query("edition") edition: String, @Query("version") version: Int): Observable<ResponseBean<AppData>>

    @GET
    fun test(@Url url: String): Observable<ResponseBean<Any>>

}