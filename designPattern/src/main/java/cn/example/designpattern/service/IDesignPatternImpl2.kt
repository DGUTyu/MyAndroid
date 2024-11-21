package cn.example.designpattern.service

import cn.example.common.service.IDesignPattern2
import cn.example.common.utils.ActivityLaunchUtils
import cn.example.designpattern.DesignPatternMainActivity
import com.google.auto.service.AutoService

@AutoService(IDesignPattern2::class)
class IDesignPatternImpl2 : IDesignPattern2 {
    override fun goDesignPatternPage2() {
        ActivityLaunchUtils.launchByApplication(DesignPatternMainActivity::class.java)
    }
}