package com.wdy.common.rx

/**
 * Created by RockQ on 2017/7/10.
 * 功能：
 */

class BaseException : Exception() {
    private var code: Int = 0
    private var msg: String = ""
    var isShow: Boolean = false

    fun getCode(): Int {
        return code
    }

    fun setCode(code: Int): BaseException {
        this.code = code
        return this
    }

    fun getMsg(): String {
        return msg
    }

    fun setMsg(msg: String): BaseException {
        this.msg = msg
        return this
    }
}
