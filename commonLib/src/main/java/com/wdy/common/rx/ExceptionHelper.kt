package com.wdy.common.rx


import android.content.DialogInterface
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.wdy.common.R
import com.wdy.common.common.ARouterPath
import com.wdy.common.common.ActivityManager
import com.wdy.common.common.BaseConstant
import com.wdy.common.presenter.view.BaseView
import com.wdy.common.utils.AppPrefsUtils
import com.wdy.common.utils.ResourceUtils
import com.wdy.common.widget.dialog.alert.NormalDialog
import com.wdy.common.widget.dialog.alert.OnNegativeClickListener
import com.wdy.common.widget.dialog.alert.OnPositiveClickListener
import org.json.JSONException
import retrofit2.HttpException
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.text.ParseException

/**
 * 作者：RockQ on 2018/8/27
 * 邮箱：qingle6616@sina.com
 *
 * msg：
 */
object ExceptionHelper {
    fun handleException(mView: BaseView, e: Throwable) {
        when (e) {
            is BaseException -> when (e.getCode()) {
                // 异地登陆
                401, 406 -> {
                    showOverdueDialog()
                }
                else -> {
                    mView.onError(e.getMsg())
                }
            }
            is SocketTimeoutException -> //连接超时
                mView.onError(ResourceUtils.getString(R.string.please_check_network))
            is HttpException -> when (e.code()) {
                // 异地登陆
                401, 406 -> {
                    showOverdueDialog()
                }
                else -> {
                    mView.onError(e.message())
                }
            }
        }
    }


    fun handleImsException(mView: BaseView, throwable: Throwable) {
        if (throwable is EOFException)
            return
        val netException = BaseException()
        var msg = ResourceUtils.getString(R.string.cannot_find_info)
        if (throwable is HttpException) {
            var exceptionInfo: ExceptionInfo? = null
            try {
                msg = throwable.response().errorBody()!!.string()
                if (msg != null && msg.startsWith("{") and (msg.endsWith("}"))) {
                    val gson = Gson()
                    exceptionInfo = gson.fromJson<ExceptionInfo>(msg, ExceptionInfo::class.java)
                }
            } catch (ignored: Exception) {
            }
            netException.setCode(throwable.code())
            if (exceptionInfo != null)
                netException.setMsg(exceptionInfo.message)

        } else if (throwable is JsonParseException
                || throwable is JSONException
                || throwable is ParseException) {
            netException.setMsg(ResourceUtils.getString(R.string.cannot_find_info)).setCode(ExceptionEngine.PARSE_ERROR)
        } else if (throwable is ConnectException || throwable is SocketTimeoutException) {
            netException.setMsg(ResourceUtils.getString(R.string.please_check_network)).setCode(ExceptionEngine.NETWORK_ERROR)  //视为网络错误
        } else
            netException.setMsg(msg).setCode(ExceptionEngine.UNKNOWN)
        handleException(mView, netException)
    }

    /**
     *
     * 登陆过期对话框显示
     */
    private var isOverDueShow = false

    private fun showOverdueDialog() {
        if (!isOverDueShow) {
            NormalDialog.Builder(ActivityManager.instance.getTopActivity())
                    .setTitle(ResourceUtils.getString(R.string.downline))
                    .setMessage(ResourceUtils.getString(R.string.account_login_another_place))
                    .setPositiveText(ResourceUtils.getString(R.string.exit))
                    .setNegativeText(ResourceUtils.getString(R.string.login_in_again))
                    .setCancelable(false)
                    .setOnDismissListener(DialogInterface.OnDismissListener { isOverDueShow = false })
                    .setOnNegativeClickListener(object : OnNegativeClickListener {
                        override fun onClick(dialog: NormalDialog, view: View) {
                            dialog.dismiss()
                            deleteDatas()
                            ARouter.getInstance()
                                    .build(ARouterPath.PATH_LOGIN)
                                    .withTransition(R.anim.scale_enter, R.anim.slide_still)
                                    .navigation()

                        }
                    })
                    .setOnPositiveClickListener(object : OnPositiveClickListener {
                        override fun onClick(dialog: NormalDialog, view: View) {
                            dialog.dismiss()
                            deleteDatas()
                            ActivityManager.instance.finishAllActivity()
                        }
                    })
                    .show()
            isOverDueShow = true
        }
    }

    /**
     * 删除app缓存，除了保存的用户名，其他都清空
     */
    private fun deleteDatas() {
        ActivityManager.instance.finishAllActivity()
        AppPrefsUtils.putBoolean(BaseConstant.IS_LOGIN, false)
        AppPrefsUtils.putString(BaseConstant.userInfo, "")
        AppPrefsUtils.putString(BaseConstant.token, "")
        AppPrefsUtils.putString(BaseConstant.userId, "")
    }
}