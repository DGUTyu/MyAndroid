package cn.example.router.utils

import android.util.Log
import cn.example.router.BuildConfig

/**
 *用 ServiceLoader 加载服务
 */
object ServiceLoaderUtils {

    /**
     * 使用 ServiceLoader 加载服务
     *
     * @param service 服务接口的 Class
     * @return 加载到的第一个服务实现，或 null（如果没有找到实现）
     */
    fun <S> load(service: Class<S>): S? {
        return try {
            val iterator = java.util.ServiceLoader.load(service).iterator()
            if (iterator.hasNext()) iterator.next() else null
        } catch (e: Exception) {
            e.printStackTrace()
            logError(service.name, e)
            null
        }
    }

    /**
     * 日志记录
     */
    private fun logError(tag: String, e: Exception) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, e.toString())
        }
    }
}
