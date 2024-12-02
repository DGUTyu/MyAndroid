package cn.example.base.manager

import cn.example.base.service.ICommon
import cn.example.base.service.IDesignPattern
import cn.example.base.service.IRetrofit
import cn.example.base.utils.ServiceLoaderUtils

class RouterManager {

    private class InstanceHolder {
        companion object {
            val INSTANCE = RouterManager()
        }
    }

    companion object {
        fun getInstance(): RouterManager {
            return InstanceHolder.INSTANCE
        }
    }

    private var iDesignPattern: IDesignPattern? = null

    private fun getDesignPatternService(): IDesignPattern? {
        if (iDesignPattern == null) {
            iDesignPattern = ServiceLoaderUtils.load(IDesignPattern::class.java)
        }
        return iDesignPattern
    }

    fun goDesignPatternPage() {
        getDesignPatternService()?.goDesignPatternPage()
    }

    private var iCommon: ICommon? = null

    private fun getCommonService(): ICommon? {
        if (iCommon == null) {
            iCommon = ServiceLoaderUtils.load(ICommon::class.java)
        }
        return iCommon
    }

    fun goCommonPage() {
        getCommonService()?.goCommonPage()
    }

    private var iRetrofit: IRetrofit? = null

    private fun getRetrofitService(): IRetrofit? {
        if (iRetrofit == null) {
            iRetrofit = ServiceLoaderUtils.load(IRetrofit::class.java)
        }
        return iRetrofit
    }

    fun goRetrofitPage() {
        getRetrofitService()?.goRetrofitPage()
    }
}