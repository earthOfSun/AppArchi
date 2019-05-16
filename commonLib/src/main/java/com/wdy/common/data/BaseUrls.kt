package com.wdy.common.data

import android.text.TextUtils
import com.wdy.common.utils.StringUtils

/**
 * 作者：RockQ on 2018/6/11
 * 邮箱：qingle6616@sina.com
 *
 * msg：
 */
object BaseUrls {
    //        private var mBaseUrl = "http://www.s-cec.com:8861/ims/"
//    private var mBaseUrl: String = "http://115.238.41.78:8861/ims2.7/"
//    private var mBaseUrl: String = "http://192.168.0.162:8080/device/"
//    private var mBaseUrl: String = "http://192.168.0.80:8080/device/"
    private var mBaseUrl: String = "https://media.w3.org/"
//    private var mBaseUrl: String = "http://192.168.0.100:8080/device/"//测试环境

    fun setBaseUrl(baseUrl: String) {
        this.mBaseUrl = baseUrl
    }

    private var mTempBaseUrl: String? = null
    fun setTempBaseUrl(baseUrl: String) {
        this.mTempBaseUrl = baseUrl
    }

    fun getBaseUrl(): String {
        if (!StringUtils.isNullOrEmpty(mTempBaseUrl)) {
            val tempUrl = mTempBaseUrl!!
            mTempBaseUrl = null
            return tempUrl
        }
        return mBaseUrl
    }

    fun getUrl(url: String?): String {
        if (!TextUtils.isEmpty(url))
            return mBaseUrl + url
        return ""
    }
}