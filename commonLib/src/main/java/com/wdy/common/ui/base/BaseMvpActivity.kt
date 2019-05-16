package com.wdy.common.ui.base

import android.os.Bundle
import com.wdy.common.presenter.BasePresenter
import com.wdy.common.presenter.view.BaseView
import com.wdy.common.widget.dialog.ProgressDialog


/**
 * 作者：RockQ on 2018/6/11
 * 邮箱：qingle6616@sina.com
 *
 * msg：
 */
abstract class BaseMvpActivity<T : BasePresenter<*>> : BaseActivity(), BaseView {
    /**
     * MVP P层
     */
    lateinit var mPresenter: T
    /**
     * 显示等待对话框
     */
    lateinit var mProgressLoading: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenterAndView()
        mProgressLoading = ProgressDialog.create(this)
        mPresenter.mProvider = this
        mPresenter.context = this
    }

    override fun showLoading() {
        mProgressLoading.showLoading()
    }

    override fun hideLoading() {
        mProgressLoading.hideLoading()
    }

    override fun onError(text: String) {
        toast(text)
    }

    /**
     * 初始化 presenter view
     * 避免 view 强转，所以把 mvp view的初始化放到该方法中
     */
    abstract fun initPresenterAndView()
}