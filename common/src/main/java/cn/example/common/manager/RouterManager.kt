package cn.example.common.manager

import cn.example.common.service.IDesignPattern
import cn.example.common.service.IDesignPattern2
import cn.example.common.utils.ServiceLoaderUtils

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

    private var iDesignPattern2: IDesignPattern2? = null

    private fun getDesignPatternService2(): IDesignPattern2? {
        if (iDesignPattern2 == null) {
            iDesignPattern2 = ServiceLoaderUtils.load(IDesignPattern2::class.java)
        }
        return iDesignPattern2
    }

    fun goDesignPatternPage2() {
        getDesignPatternService2()?.goDesignPatternPage2()
    }
}