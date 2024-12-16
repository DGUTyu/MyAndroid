package cn.example.encrypte.serviceImpl

import cn.example.base.service.IEncrypte
import cn.example.base.utils.ActivityLaunchUtils
import cn.example.encrypte.activity.EncrypteMainActivity
import com.google.auto.service.AutoService

@AutoService(IEncrypte::class)
class IEncrypteImpl : IEncrypte {
    override fun goEncryptePage() {
        ActivityLaunchUtils.launchByApplication(EncrypteMainActivity::class.java)
    }
}