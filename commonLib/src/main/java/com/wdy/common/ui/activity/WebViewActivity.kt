package com.wdy.common.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebSettings

import com.wdy.common.R
import com.wdy.common.data.BaseUrls
import com.wdy.common.ui.base.BaseActivity
import com.wdy.common.utils.ScreenFitUtils
import com.wdy.common.utils.StringUtils
import kotlinx.android.synthetic.main.activity_web_view.*
import java.util.*


/**
 * 作者：RockQ on 2018/6/13
 * 邮箱：qingle6616@sina.com
 *
 * msg：
 */
class WebViewActivity : BaseActivity() {
    companion object {
        fun launch(context: Context, url: String, strTitle: String) {
            if (StringUtils.isNullOrEmpty(url)) {
                return
            }
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url", url)
            intent.putExtra("title", strTitle)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            context.startActivity(intent)

        }
    }

    private var title: String? = null
    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        ScreenFitUtils.auto(this)
        mTitleBar.addLeftBackImageButton().setOnClickListener { finish() }
        url = intent.getStringExtra("url")
        title = intent.getStringExtra("title")
        title?.let { mTitleBar.setTitle(it) }
        initView()

    }

    private fun initView() {
        val webSettings = mWebView.settings
        webSettings.useWideViewPort = true//设置webview推荐使用的窗口
        webSettings.loadWithOverviewMode = true//设置webview加载的页面的模式
        webSettings.displayZoomControls = false//隐藏webview缩放按钮
//        webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本
//        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.builtInZoomControls = true // 设置显示缩放按钮
        webSettings.setSupportZoom(true) // 支持缩放
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        if (url.startsWith("http://"))
            mWebView.loadUrl(url)
        else
            mWebView.loadUrl(String.format(Locale.CHINA, "%s%s", BaseUrls.getBaseUrl(), url))
    }
}