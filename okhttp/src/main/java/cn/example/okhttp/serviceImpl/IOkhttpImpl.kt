package cn.example.okhttp.serviceImpl

import cn.example.base.service.IOkhttp
import cn.example.base.utils.ActivityLaunchUtils
import cn.example.okhttp.activity.OkhttpMainActivity
import com.google.auto.service.AutoService

@AutoService(IOkhttp::class)
class IOkhttpImpl: IOkhttp{
    override fun goOkhttpPage() {
        ActivityLaunchUtils.launchByApplication(OkhttpMainActivity::class.java)
    }
}