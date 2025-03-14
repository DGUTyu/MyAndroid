package cn.example.myandroid.app

import android.os.Build
import androidx.annotation.RequiresApi
import cn.example.task.launchstarter.TaskDispatcher
import cn.example.base.base.BaseApplication


class AppApplication : BaseApplication() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()

        TaskDispatcher.init(this)
        val dispatcher = TaskDispatcher.createInstance()
        dispatcher.start()
        dispatcher.await()
    }
}