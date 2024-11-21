package cn.example.router.base

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * 在 Kotlin 中，类和方法默认是 final 的，不能被继承或重写。要支持继承，必须将类和方法标记为 open。
 */
open class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setApplication(this)
    }

    companion object {
        @Volatile
        private var sInstance: BaseApplication? = null

        /**
         * 当主工程没有继承BaseApplication时，可以使用setApplication方法初始化BaseApplication
         */
        @Synchronized
        fun setApplication(application: BaseApplication) {
            sInstance = application
            //注册监听每个activity的生命周期,便于堆栈式管理
            application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    //AppManager.getAppManager().addActivity(activity);
                }

                override fun onActivityStarted(activity: Activity) {}
                override fun onActivityResumed(activity: Activity) {}
                override fun onActivityPaused(activity: Activity) {}
                override fun onActivityStopped(activity: Activity) {}
                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
                override fun onActivityDestroyed(activity: Activity) {
                    //AppManager.getAppManager().removeActivity(activity);
                }
            })
        }

        /**
         * 获取当前 app 的 Application 实例
         * get() = 是一种简写的方式，用于定义属性的自定义 getter。
         * 通过这种方式，可以对属性的获取逻辑进行定制，而无需定义整个 get() 方法。
         */
        val instance: BaseApplication
            get() = sInstance
                    ?: throw NullPointerException("Please inherit BaseApplication or call setApplication.")
    }
}
