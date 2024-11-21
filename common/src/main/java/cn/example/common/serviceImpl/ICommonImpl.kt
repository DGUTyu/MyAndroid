package cn.example.common.serviceImpl

import cn.example.common.CommonMainActivity
import cn.example.router.service.ICommon
import cn.example.router.utils.ActivityLaunchUtils
import com.google.auto.service.AutoService

@AutoService(ICommon::class)
class ICommonImpl : ICommon {
    override fun goCommonPage() {
        ActivityLaunchUtils.launchByApplication(CommonMainActivity::class.java)
    }
}