package cn.example.designpattern.mvp.utils

import android.util.Log
import cn.example.designpattern.mvp.http.RequestParam
import cn.example.base.utils.StringUtil
import com.google.gson.Gson

/**
 * 加密加签示例工具类
 */
object CryptoDemoUtil {

    fun addData(requestParam: RequestParam, sm4A: String): String {
        return requestParam.toString()
    }

    fun addData(requestParam: RequestParam, type: Int, sm4_A: String): String? {
        return try {
            val map1 = mutableMapOf<String, String>()
            val map2 = mutableMapOf<String, String>()

            val sm4_B = "SpaySMUtil.SM2EncryptNative(sm4_A, sm2PublicKey)"

            when (type) {
                1 -> {
                    // 图片视频流上传
                    map1.putAll(jsonToMap(requestParam.toString())!!)
                    map1["secretKey"] = sm4_B
                    map1["shareKey"] = sm4_A
                    map2.putAll(map1)
                }

                2 -> {
                    // dh秘钥交换
                    map1.putAll(jsonToMap(requestParam.toString())!!)
                    map2.putAll(map1)
                }

                else -> {
                    // 普通接口
                    val dataStr2 = "SpaySMUtil.SM4EncryptNative(requestParam.toString(), sm4_A)"
                    map1["data"] = dataStr2
                    map1["secretKey"] = sm4_B
                    map1["shareKey"] = sm4_A
                    map2["data"] = dataStr2
                    map2["secretKey"] = sm4_B
                }
            }
            //进行参数排序
            val param = createRsaParam(map1)
            Log.i("Tag", "参数排序==$param")
            val sm3SignStr = "SpaySMUtil.SM3HashSignNative(map1)"

            if (!StringUtil.isEmptyOrNull(sm3SignStr)) {
                map2["smSign"] = sm3SignStr
            } else {
                Log.i("Tag", "sm3哈希签名失败")
            }

            val data = Gson().toJson(map2)
            if (!StringUtil.isEmptyOrNull(data)) {
                data
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("Tag", e.message.orEmpty())
            null
        }
    }

    fun jsonToMap(jsonStr: String): Map<String, String>? {
        val gson = Gson() // 初始化 Gson 实例
        val type = object : com.google.gson.reflect.TypeToken<Map<String, String>>() {}.type
        return gson.fromJson<Map<String, String>>(jsonStr, type)
    }

    fun createRsaParam(params: Map<String, String>): String {
        val buf = StringBuilder()
        buildPayParams(buf, params, false)
        return buf.toString()
    }

    fun buildPayParams(sb: StringBuilder, payParams: Map<String, String>, encoding: Boolean) {
        val keys = payParams.keys.sorted() // 排序键
        for (key in keys) {
            sb.append(key).append("=")
            if (encoding) {
                sb.append(urlEncode(payParams[key] ?: ""))
            } else {
                sb.append(payParams[key])
            }
            sb.append("&")
        }
        if (sb.isNotEmpty()) {
            // 移除最后的 '&'
            sb.setLength(sb.length - 1)
        }
    }

    fun urlEncode(str: String): String {
        return try {
            java.net.URLEncoder.encode(str, "UTF-8")
        } catch (e: Exception) {
            str
        }
    }
}