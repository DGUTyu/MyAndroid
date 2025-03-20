package cn.example.performance.serviceImpl

import cn.example.base.service.IPerformance
import cn.example.base.utils.ActivityLaunchUtils
import cn.example.performance.activity.PerformanceMainActivity
import com.google.auto.service.AutoService

@AutoService(IPerformance::class)
class IPerformanceImpl : IPerformance {
    override fun goPerformancePage() {
        ActivityLaunchUtils.launchByApplication(PerformanceMainActivity::class.java)
    }
}