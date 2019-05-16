package com.wdy.common.utils

import android.support.annotation.Nullable
import java.io.Closeable
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

/**
 * 作者：RockQ on 2018/6/12
 * 邮箱：qingle6616@sina.com
 *
 * msg：关于字符串的相关操作
 */
object StringUtils {
    /**
     * 获取数值的位数，例如9返回1，99返回2，999返回3
     *
     * @param number 要计算位数的数值，必须>0
     * @return 数值的位数，若传的参数小于等于0，则返回0
     */
    fun getNumberDigits(number: Int): Int {
        return if (number <= 0) 0 else (Math.log10(number.toDouble()) + 1).toInt()
    }


    fun getNumberDigits(number: Long): Int {
        return if (number <= 0) 0 else (Math.log10(number.toDouble()) + 1).toInt()
    }

    /**
     * 规范化价格字符串显示的工具类
     *
     * @param price 价格
     * @return 保留两位小数的价格字符串
     */
    fun regularizePrice(price: Float): String {
        return String.format(Locale.CHINESE, "%.2f", price)
    }

    /**
     * 规范化价格字符串显示的工具类
     *
     * @param price 价格
     * @return 保留两位小数的价格字符串
     */
    fun regularizePrice(price: Double): String {
        return String.format(Locale.CHINESE, "%.2f", price)
    }


    fun isNullOrEmpty(@Nullable string: CharSequence?): Boolean {

        return string == null || string.trim().isEmpty() || string.toString().equals("null", true)
    }

    fun close(c: Closeable?) {
        if (c != null) {
            try {
                c.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    fun objectEquals(a: Any?, b: Any): Boolean {
        return a === b || a != null && a == b
    }

    fun constrain(amount: Int, low: Int, high: Int): Int {
        return if (amount < low) low else if (amount > high) high else amount
    }

    fun constrain(amount: Float, low: Float, high: Float): Float {
        return if (amount < low) low else if (amount > high) high else amount
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     * @param strURL url地址
     * @return url请求参数部分
     * @author lzf
     */
    fun truncateUrlPage(strURL: String): String? {
        var strURL = strURL
        var strAllParam: String? = null
        var arrSplit: Array<String>? = null
        strURL = strURL.trim { it <= ' ' }.toLowerCase()
        arrSplit = strURL.split("[?]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (strURL.length > 1) {
            if (arrSplit.size > 1) {
                for (i in 1 until arrSplit.size) {
                    strAllParam = arrSplit[i]
                }
            }
        }
        return strAllParam
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     * @param URL  url地址
     * @return  url请求参数部分
     * @author lzf
     */
    fun urlSplit(URL: String): Map<String, String> {
        val mapRequest = HashMap<String, String>()
        var arrSplit: Array<String>? = null
        val strUrlParam = truncateUrlPage(URL) ?: return mapRequest
        arrSplit = strUrlParam.split("[&]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (strSplit in arrSplit) {
            var arrSplitEqual: Array<String>? = null
            arrSplitEqual = strSplit.split("[=]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            //解析出键值
            if (arrSplitEqual.size > 1) {
                //正确解析
                mapRequest[arrSplitEqual[0]] = arrSplitEqual[1]
            } else {
                if (arrSplitEqual[0] !== "") {
                    //只有参数没有值，不加入
                    mapRequest[arrSplitEqual[0]] = ""
                }
            }
        }
        return mapRequest
    }

    /**
     * 获取url中的出去参数后的url
     * 使用http:baidu.com/serarch.app?type=1
     * 或者search.app?type=1
     */
    fun getUrl(url: String): String {
        return url.split("?")[0]
    }

    /**
     * 判断字符串中是否包含中文
     * @param str
     * 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    fun isContainChinese(str: String): Boolean {
        val p = Pattern.compile("[\u4e00-\u9fa5]")
        val m = p.matcher(str)
        return m.find()
    }
}