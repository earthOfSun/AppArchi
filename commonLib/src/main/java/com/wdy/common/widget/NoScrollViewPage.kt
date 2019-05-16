package com.wdy.common.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * 作者：RockQ on 2018/9/7
 * 邮箱：qingle6616@sina.com
 *
 * msg：
 */
class NoScrollViewPage constructor(context: Context, attributeSet: AttributeSet) : ViewPager(context, attributeSet) {

    private var noScroll = false

    fun setNoScroll(noScroll: Boolean) {
        this.noScroll = noScroll
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if (noScroll)
            false
        else
            super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(me: MotionEvent): Boolean {
        return if (noScroll)
            false
        else
            super.onInterceptTouchEvent(me)
    }
}