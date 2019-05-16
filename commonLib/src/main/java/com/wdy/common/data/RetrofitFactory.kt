package com.wdy.common.data

import android.text.TextUtils
import com.wdy.common.common.BaseConstant
import com.wdy.common.utils.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 作者：RockQ on 2018/6/11
 * 邮箱：qingle6616@sina.com
 *
 * msg：
 */
class RetrofitFactory private constructor() {
    companion object {
        val instance: RetrofitFactory by lazy { RetrofitFactory() }
    }

    /**
     * retrofit对象
     */
    private val retrofit: Retrofit
    /**
     * 拦截器
     */
    private val interceptor: Interceptor

    init {
        interceptor = Interceptor { chain ->
            val request = chain.request()
                    .newBuilder()
//                    .addHeader("Content_Type", "application/json")
//                    .addHeader("charset", "UTF-8")
                    .addHeader("ip", TextUtils.isEmpty(NetworkUtils.getIp()).toString())
                    .addHeader("clientKey", PhoneUtils.getPhoneType())
//                    .addHeader("token", AppPrefsUtils.getString(BaseConstant.token))
//                    .addHeader("token", "eyJhbGciOiJIUzI1NiJ9.eyJleHQiOjE1NTM2Njc4MDQ3NTMsInN5c1VzZXJOaWNrTmFtZSI6InlleGluaGFpIiwiY2xpZW50X2tleSI6IkhVQVdFSVthbmRyb2lkIDguMC4wXSIsInN5c191c2VyX2lkIjoiZjA5MGYxODM2NjI0Yzc5MTAxNjY1MWIyMGFmZDAyMjYifQ.w1q_mSb2IzgKty3Zra0uBWPwLAjOqG1tSlKwDzVQUcA")
                    .build()

            chain.proceed(request)
        }
        //Retrofit实例化
        retrofit = Retrofit.Builder()
                .baseUrl(BaseUrls.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(initClient()!!)
                .build()
    }

    private fun initClient(): OkHttpClient? {
        return OkHttpClient
                .Builder()
                .addInterceptor(initInterceptor())
                .addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()
    }

    private fun initInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (AppUtils.isAPKInDebug(ResourceUtils.getContext())) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return interceptor
    }

    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

}