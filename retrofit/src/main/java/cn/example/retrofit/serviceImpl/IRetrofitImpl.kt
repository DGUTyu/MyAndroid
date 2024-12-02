package cn.example.retrofit.serviceImpl

import cn.example.base.service.IRetrofit
import cn.example.base.utils.ActivityLaunchUtils
import cn.example.retrofit.acitivty.RetrofitMainActivity
import com.google.auto.service.AutoService

@AutoService(IRetrofit::class)
class IRetrofitImpl: IRetrofit{
    override fun goRetrofitPage() {
        ActivityLaunchUtils.launchByApplication(RetrofitMainActivity::class.java)
    }
}