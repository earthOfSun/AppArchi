package com.wdy.common.data

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*
import okhttp3.RequestBody
import retrofit2.http.POST
import retrofit2.http.Multipart




/**
 * 作者：RockQ on 2018/6/11
 * 邮箱：qingle6616@sina.com
 *
 * msg：服务访问 service
 */
interface BaseService {
    @POST
    @FormUrlEncoded
    fun post(@Url url: String, @FieldMap requestMaps: Map<String, String>): Observable<ResponseBody>

    @POST
    @FormUrlEncoded
    fun postMapByAny(@Url url: String, @FieldMap requestMaps: @JvmSuppressWildcards Map<String, Any>): Observable<ResponseBody>

    @POST
    @FormUrlEncoded
    fun post(@Url url: String): Observable<ResponseBody>

    @GET
    fun get(@Url url: String, @QueryMap requestMaps: Map<String, String>): Observable<ResponseBody>

    @GET
    fun getMapByAny(@Url url: String, @QueryMap requestMaps: @JvmSuppressWildcards Map<String, Any>): Observable<ResponseBody>

    @GET
    operator fun get(@Url url: String): Observable<ResponseBody>

    /**
     * 下载文件
     */
    @Streaming//大文件时要加不然会OOM
    @GET
    fun downloadFile(@Url url: String): Observable<ResponseBody>

    @DELETE
    fun delete(@Url url: String, @QueryMap requestMaps: @JvmSuppressWildcards Map<String, Any>): Observable<ResponseBody>

    @DELETE
    fun delete(@Url url: String): Observable<ResponseBody>

    @Multipart
    @POST
    fun uploadFiles(@Url url: String, @Part parts: List<MultipartBody.Part>): Observable<ResponseBody>

    @Multipart
    @POST
    fun upload(
        @Url url: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Observable<ResponseBody>

    @Streaming
    @GET
    fun downFile(@Url url: String, @QueryMap maps: Map<String, String>): Observable<ResponseBody>
}