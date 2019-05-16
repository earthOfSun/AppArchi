package com.wdy.common.data.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by 上海中电
 * on 2016/12/30
 */
@Parcelize
data class PageListJson<T:Parcelable>(
        var currentPage: Int = 0,
        var totalPage: Int = 0,
        var pageSize: Int = 0,
        var totalCount: Int = 0,
        var objs: MutableList<T>? = null
) : Parcelable
