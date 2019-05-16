package com.wdy.common.widget.dialog.alert

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import com.wdy.common.R
import com.wdy.common.widget.view.alpha.AlphaButton

/**
 * 作者：RockQ on 2018/9/18
 * 邮箱：qingle6616@sina.com
 *
 * msg：
 */
class NormalDialog private constructor(context: Context) : AlertDialog(context) {
    private var mTvTitle: TextView
    private var mTvMessage: TextView
    private var mBtnPositive: AlphaButton
    private var mBtnNegative: AlphaButton

    init {
        val dialogView = View.inflate(context, R.layout.layout_alert_dialog, null)
        mTvTitle = dialogView.findViewById<TextView>(R.id.mTvTitle)
        mTvMessage = dialogView.findViewById<TextView>(R.id.mTvMessage)
        mBtnPositive = dialogView.findViewById<AlphaButton>(R.id.mBtnPositive)
        mBtnNegative = dialogView.findViewById<AlphaButton>(R.id.mBtnNegative)
        setView(dialogView)
    }

    override fun setMessage(message: CharSequence?) {
        mTvMessage.text = message
    }

    override fun setTitle(title: CharSequence?) {
        mTvTitle.text = title
    }

    fun setPositiveText(message: CharSequence?) {
        mBtnPositive.text = message
    }

    fun setNegativeText(title: CharSequence?) {
        mBtnNegative.text = title
    }

    fun setOnPositiveClickListener(listener: OnPositiveClickListener) {
        mBtnPositive.setOnClickListener {
            listener.onClick(this, mBtnPositive)
        }
    }

    fun setOnNegativeClickListener(listener: OnNegativeClickListener) {
        mBtnNegative.setOnClickListener {
            listener.onClick(this, mBtnNegative)
        }

    }

    class Builder constructor(context: Context) {

        private var dialog = NormalDialog(context)

        fun setMessage(message: CharSequence?): Builder {
            dialog.setMessage(message)
            return this
        }

        fun setTitle(title: CharSequence?): Builder {
            dialog.setTitle(title)
            return this
        }

        fun setPositiveText(text: CharSequence?): Builder {
            dialog.setPositiveText(text)
            return this
        }

        fun setNegativeText(text: CharSequence?): Builder {
            dialog.setNegativeText(text)
            return this
        }

        fun setOnPositiveClickListener(listener: OnPositiveClickListener): Builder {
            dialog.setOnPositiveClickListener(listener)
            return this
        }

        fun setOnNegativeClickListener(listener: OnNegativeClickListener): Builder {
            dialog.setOnNegativeClickListener(listener)
            return this
        }

        fun setCancelable(cancelable: Boolean): Builder {
            dialog.setCancelable(cancelable)
            return this
        }

        fun setOnDismissListener(listener: DialogInterface.OnDismissListener): Builder {
            dialog.setOnDismissListener(listener)
            return this
        }

        fun show() {
            dialog.show()
        }
    }
}