package com.wdy.common.widget.view.alpha

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

import com.wdy.common.widget.viewHelper.AlphaViewHelper

/**
 * 作者：RockQ on 2018/6/12
 * 邮箱：qingle6616@sina.com
 */
open class AlphaFrameLayout(context: Context?, attrs: AttributeSet?, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?) : this(context, null, 0)

    private fun getAlphaViewHelper(): AlphaViewHelper {
        return AlphaViewHelper(this)
    }

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        getAlphaViewHelper().onPressedChanged(this, pressed)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        getAlphaViewHelper().onEnabledChanged(this, enabled)
    }

    /**
     * 设置是否要在 press 时改变透明度
     *
     * @param changeAlphaWhenPress 是否要在 press 时改变透明度
     */
    fun setChangeAlphaWhenPress(changeAlphaWhenPress: Boolean) {
        getAlphaViewHelper().setChangeAlphaWhenPress(changeAlphaWhenPress)
    }

    /**
     * 设置是否要在 disabled 时改变透明度
     *
     * @param changeAlphaWhenDisable 是否要在 disabled 时改变透明度
     */
    fun setChangeAlphaWhenDisable(changeAlphaWhenDisable: Boolean) {
        getAlphaViewHelper().setChangeAlphaWhenDisable(changeAlphaWhenDisable)
    }
}