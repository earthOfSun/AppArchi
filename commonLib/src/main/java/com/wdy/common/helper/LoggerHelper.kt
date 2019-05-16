package com.wdy.common.helper

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

/**
 * Created by 上海中电
 * on 2017/1/4
 */

object LoggerHelper {
    val TAG = "XQL_LOGGER"
    private var isShow: Boolean = false
    @JvmStatic
    fun isShow(isShow: Boolean) {
        Logger.addLogAdapter(AndroidLogAdapter())
        LoggerHelper.isShow = isShow
    }

    /**
     * log.i
     *
     * @param msg
     */
    @JvmStatic
    fun i(msg: String) {
        if (isShow) {
            Logger.i(msg)
        }
    }

    /**
     * log.d
     *
     * @param msg
     */
    @JvmStatic
    fun d(msg: String) {
        if (isShow) {
            Logger.d(msg)
        }
    }

    /**
     * log.w
     *
     * @param msg
     */
    @JvmStatic
    fun w(msg: String) {
        if (isShow) {
            Logger.w(msg)
        }
    }

    /**
     * log.e
     *
     * @param msg
     */
    @JvmStatic
    fun e(msg: String) {
        Logger.e(msg)
    }

    @JvmStatic
    fun e(e: Throwable) {
        Logger.e(e, "")
    }

    /**
     * log.json
     */
    @JvmStatic
    fun json(json: String) {
        if (isShow) {
            Logger.json(json)
        }
    }

    /**
     * log.json
     */
    @JvmStatic
    fun xml(xml: String) {
        if (isShow) {
            Logger.xml(xml)
        }
    }
}
