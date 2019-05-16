package com.wdy.common.common

import android.app.Activity
import android.view.View

import com.wdy.common.utils.StringUtils
import com.wdy.common.utils.ToastUtils

/**
 * 基础View，一般情况下所有的布局View继承该View
 */

abstract class BaseViewPage(var mContext: Activity) {
    var mRootView: View// 菜单详情页根布局


    init {
        mRootView = initView()
    }

    // 初始化布局,必须子类实现
    abstract fun initView(): View

    // 初始化数据
    open fun initData() {}

    protected fun toast(content: String) {
        if (!StringUtils.isNullOrEmpty(content))
            ToastUtils.toast(mContext, content)
    }

    protected fun toast(resId: Int) {
        ToastUtils.toast(mContext, mContext.resources.getString(resId))
    }

    protected fun toast(redId: Int, type: Int) {
        ToastUtils.toast(mContext, mContext.resources.getString(redId), type)

    }

    protected fun toast(content: String, type: Int) {
        if (!StringUtils.isNullOrEmpty(content))
            ToastUtils.toast(mContext, content, type)
    }
}
