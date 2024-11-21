package cn.example.common.utils

import android.app.Activity
import android.content.Intent
import cn.example.common.base.BaseApplication

object ActivityLaunchUtils {

    /**
     * 通过 Application 启动 Activity（无参数）
     */
    fun launchByApplication(clazz: Class<out Activity>) {
        launchByApplication(Intent(BaseApplication.instance, clazz))
    }

    /**
     * 通过 Application 启动 Activity（带参数）
     */
    fun launchByApplication(clazz: Class<out Activity>, intent: Intent) {
        launchByApplication(intent.setClass(BaseApplication.instance, clazz))
    }

    /**
     * 通过 Application 启动 Activity（直接使用 Intent）
     */
    fun launchByApplication(intent: Intent) {
        BaseApplication.instance.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /**
     * 通过 Activity 启动 Activity
     */
    fun launchByActivity(activity: Activity, clazz: Class<out Activity>) {
        if (!activity.isFinishing) {
            activity.startActivity(Intent(activity, clazz))
        }
    }
}
