package com.cestco.viewlib.StepView

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FlowChart(
    var name :String ?= null,
    var topName :String ?= null,
    var bottomName :String ?= null,
    var time :String ?= null

):Parcelable