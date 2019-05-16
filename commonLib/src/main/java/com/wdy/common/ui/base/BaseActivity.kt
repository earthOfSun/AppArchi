package com.wdy.common.ui.base

import android.app.Activity
import android.os.Bundle
import android.support.annotation.StringRes
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.wdy.common.common.ActivityManager
import com.wdy.common.utils.StringUtils
import com.wdy.common.utils.ToastUtils

/**
 * 作者：RockQ on 2018/6/11
 * 邮箱：qingle6616@sina.com
 *
 * msg：
 */
abstract class BaseActivity : RxAppCompatActivity() {
    protected lateinit var mContext: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        ActivityManager.instance.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.instance.finishActivity(this)
    }

    protected fun toast(str: String) {
        if (!StringUtils.isNullOrEmpty(str))
            ToastUtils.toast(this, str)
    }

    protected fun toast(@StringRes strRes: Int) {
            ToastUtils.toast(this, getString(strRes))
    }
}