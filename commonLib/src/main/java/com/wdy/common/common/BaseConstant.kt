package com.wdy.common.common

/**
 *
 * msg：全局通用常量
 */
object BaseConstant {

    /**
     * SP 数据表的名称
     */
    const val TABLE_PREFS = "ics"

    const val LOAD_DATA_NORMAL = 1
    const val LOAD_DATA_REFRESH = 2
    const val LOAD_DATA_LOAD_MORE = 3
    const val LOAD_DATA_REFRESH_LATE = 4

    /**
     * 是否登陆
     */
    const val IS_LOGIN: String = "isLogin"

    const val userId: String = "userId"
    const val token: String = "token"
    const val userInfo: String = "userInfo"
    const val userName: String = "userName"
    const val phone: String = "phone"
}