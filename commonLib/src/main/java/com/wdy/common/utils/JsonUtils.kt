package com.wdy.common.utils


import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.wdy.common.data.domain.NormalListObject
import com.wdy.common.data.domain.NormalObject
import com.wdy.common.data.domain.PageListJson
import com.wdy.common.data.domain.SimpleObject
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * 作者：RockQ on 2018/9/6
 * 邮箱：qingle6616@sina.com
 *
 * msg：
 */
object JsonUtils {
    /**
     * json -> 简单实体类
     *
     * @param jsonStr :json字符串
     * @param clazz : 转换的对象
     */
    @JvmStatic
    fun <T : Parcelable> json2NormalObject(jsonStr: String, clazz: Class<*>): NormalObject<T>? {
        if (jsonStr.startsWith("{") and (jsonStr.endsWith("}"))) {
            val gson = Gson()
            val objectType = type(NormalObject::class.java, clazz)
            return gson.fromJson(jsonStr, objectType)
        }
        return null

    }

    @JvmStatic
    fun json2SimpleObject(jsonStr: String): SimpleObject? {
        return try {
            val gson = Gson()
            gson.fromJson(jsonStr, SimpleObject::class.java)
        } catch (e: Exception) {
            null
        }

    }

    /**
     * 类型整合
     */
    @JvmStatic
    private fun type(raw: Class<*>, vararg args: Type): ParameterizedType {

        return object : ParameterizedType {
            override fun getOwnerType(): Type? {
                return null
            }

            override fun getActualTypeArguments(): Array<out Type> {
                return args
            }

            override fun getRawType(): Type {
                return raw
            }
        }
    }

    fun <T : Parcelable> json2Object(jsonStr: String, clazz: Class<T>): T? {
        if (jsonStr.startsWith("{") and (jsonStr.endsWith("}"))) {
            if (StringUtils.isNullOrEmpty(jsonStr)) return null
            val gson = Gson()
            return gson.fromJson(jsonStr, clazz)
        }
        return null

    }

    @Synchronized
    fun <T : Parcelable> json2PageList(result: String, clazz: Class<T>): PageListJson<T>? {
        if (result.startsWith("{") and (result.endsWith("}"))) {
            val gson = Gson()
            val objectType = type(PageListJson::class.java, clazz)
            return gson.fromJson<PageListJson<T>>(result, objectType)
        }
        return null
    }

    @Synchronized
    fun <T : Parcelable> json2NormalList(result: String, clazz: Class<T>): NormalListObject<T>? {
        if (result.startsWith("{") and (result.endsWith("}"))) {
            val gson = Gson()
            val objectType = type(NormalListObject::class.java, clazz)
            return gson.fromJson<NormalListObject<T>>(result, objectType)
        }
        return null
    }

    @Synchronized
    fun <T> json2List(result: String, tClass: Class<T>): ArrayList<T> {
        val datas = ArrayList<T>()
        val parser = JsonParser()
        val jsonArray = parser.parse(result).asJsonArray
        val gson = Gson()
        for (user in jsonArray) {
            //使用GSON，直接转成Bean对象
            val userBean = gson.fromJson(user, tClass)
            datas.add(userBean)
        }
        return datas


    }

}
