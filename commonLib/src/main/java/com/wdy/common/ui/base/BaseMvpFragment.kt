package com.wdy.common.ui.base

import android.os.Bundle
import com.wdy.common.presenter.BasePresenter
import com.wdy.common.presenter.view.BaseView
import com.wdy.common.utils.StringUtils
import com.wdy.common.utils.ToastUtils
import com.wdy.common.widget.dialog.ProgressDialog

/**
 * 作者：RockQ on 2018/6/13
 * 邮箱：qingle6616@sina.com
 *
 * msg：
 */
abstract class BaseMvpFragment<T : BasePresenter<*>> : BaseFragment(), BaseView {
    /**
     * MVP p层
     */
    lateinit var mPresenter: T
    /**
     * 显示延迟对话框
     */
    lateinit var mProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
        mProgressDialog = ProgressDialog.create(activity!!)
        mPresenter.context = activity!!
        mPresenter.mProvider = this
    }

    abstract fun initPresenter()
    override fun showLoading() {
        mProgressDialog.showLoading()
    }

    override fun hideLoading() {
        mProgressDialog.hideLoading()
    }

    override fun onError(text: String) {
        if (!StringUtils.isNullOrEmpty(text))
            ToastUtils.toast(text)
    }
}