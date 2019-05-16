package com.wdy.common.rx

import com.google.gson.JsonParseException
import com.wdy.common.R
import com.wdy.common.utils.ResourceUtils
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.text.ParseException

/**
 * Created by RockQ on 2017/7/10.
 * 功能：
 */

object ExceptionEngine {
    //对应HTTP的状态码
    val DATA_OVERDUE = 205
    val UNAUTHORIZED = 401
    val FORBIDDEN = 403
    val NOT_FOUND = 404
    val REQUEST_TIMEOUT = 408
    val LOGIN_OVERDUE = 406
    val INTERNAL_SERVER_ERROR = 500
    val BAD_GATEWAY = 502
    val SERVICE_UNAVAILABLE = 503
    val GATEWAY_TIMEOUT = 504
    /**
     * 未知错误
     */
    val UNKNOWN = 1000
    /**
     * 解析错误
     */
    val PARSE_ERROR = 1001
    /**
     * 网络错误
     */
    val NETWORK_ERROR = 1002

    @JvmStatic
    fun handleException(throwable: Throwable): BaseException? {
        if (throwable is EOFException)
            return null
        val netException = BaseException()
        var msg = ResourceUtils.getString(R.string.cannot_find_info)
        if (throwable is HttpException) {
            try {
                msg = throwable.response().errorBody()!!.string()
            } catch (ignored: Exception) {
            }

            netException.setCode(throwable.code())
            netException.setMsg(msg)
        } else if (throwable is JsonParseException
                || throwable is JSONException
                || throwable is ParseException) {
            netException.setMsg(ResourceUtils.getString(R.string.cannot_find_info)).setCode(PARSE_ERROR)
        } else if (throwable is ConnectException || throwable is SocketTimeoutException) {
            netException.setMsg(ResourceUtils.getString(R.string.error_connect)).setCode(NETWORK_ERROR)  //视为网络错误
        } else
            netException.setMsg(msg).setCode(UNKNOWN)
        return netException
    }
}
