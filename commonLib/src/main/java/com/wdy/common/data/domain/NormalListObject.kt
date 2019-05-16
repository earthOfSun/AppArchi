package com.wdy.common.data.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class  NormalListObject <T : Parcelable>(
        @SerializedName("msg")
        val msg: String,
        @SerializedName("code")
        val code: String,
        @SerializedName("objs")
        val objs: MutableList<T>? = null
):Parcelable