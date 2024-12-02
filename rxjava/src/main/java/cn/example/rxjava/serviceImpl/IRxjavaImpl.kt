package cn.example.rxjava.serviceImpl

import cn.example.base.service.IRxjava
import cn.example.base.utils.ActivityLaunchUtils
import cn.example.rxjava.acitivty.RxjavaMainActivity
import com.google.auto.service.AutoService

@AutoService(IRxjava::class)
class IRxjavaImpl: IRxjava{
    override fun goRxjavaPage() {
        ActivityLaunchUtils.launchByApplication(RxjavaMainActivity::class.java)
    }
}