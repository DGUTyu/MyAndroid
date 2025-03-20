package cn.example.base.manager

import cn.example.base.service.*
import cn.example.base.utils.ServiceLoaderUtils

class RouterManager {
    // 服务缓存，用于存储已加载的服务实例。即以服务类的 Class 对象作为键，服务实例作为值。
    private val serviceCache = mutableMapOf<Class<*>, Any?>()

    // 使用静态内部类实现单例
    private class InstanceHolder {
        companion object {
            val INSTANCE = RouterManager()
        }
    }

    // 在 RouterManager 类中提供 getInstance() 方法
    companion object {
        fun getInstance(): RouterManager {
            return InstanceHolder.INSTANCE
        }
    }

    // 通用获取服务实例方法，通过泛型简化
    // 通过 inline，函数的代码会在调用处直接展开，从而避免了函数调用的开销。特别适用于高阶函数，通常用来提高性能。
    // inline 函数在编译时会将函数体复制到调用点，以避免不必要的堆栈创建和函数调用。
    // reified 只能与 inline 函数一起使用，用来解决 Kotlin 泛型的类型擦除问题。
    // 在 JVM 上，泛型信息在运行时是被擦除的（也就是 List<String> 和 List<Int> 会被视为 List，失去了类型信息）。
    // reified 让编译器保留泛型类型信息，可以在运行时通过 T::class 获取类型。
    // reified 使得在 inline 函数中，泛型类型 T 变得可以访问，可以直接在代码中使用 T::class 等操作。
    // also 是一个 Kotlin 标准库函数，用来对对象进行操作并返回该对象本身。
    private inline fun <reified T> getService(): T? {
        val serviceClass = T::class.java
        return serviceCache[serviceClass] as? T ?: ServiceLoaderUtils.load(serviceClass)?.also {
            serviceCache[serviceClass] = it
        }
    }

    fun goDesignPatternPage() {
        getService<IDesignPattern>()?.goDesignPatternPage()
    }

    fun goCommonPage() {
        getService<ICommon>()?.goCommonPage()
    }

    fun goRetrofitPage() {
        getService<IRetrofit>()?.goRetrofitPage()
    }

    fun goRxjavaPage() {
        getService<IRxjava>()?.goRxjavaPage()
    }


    fun goOkhttpPage() {
        getService<IOkhttp>()?.goOkhttpPage()
    }

    fun goEncryptePage() {
        getService<IEncrypte>()?.goEncryptePage()
    }

    fun goPerformancePage() {
        getService<IPerformance>()?.goPerformancePage()
    }
}