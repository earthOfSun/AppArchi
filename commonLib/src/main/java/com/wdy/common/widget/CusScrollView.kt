package com.wdy.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

/**
 * 作者：RockQ on 2018/9/29
 * 邮箱：qingle6616@sina.com
 *
 * msg：
 */
class CusScrollView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ScrollView(context, attrs, defStyleAttr) {
    private var canScroll = true
    public fun setCanScroll(canScroll: Boolean) {
        this.canScroll = canScroll
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return canScroll && super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return canScroll && super.onTouchEvent(ev)
    }
}