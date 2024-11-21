package cn.example.designpattern.mvp.utils

import cn.example.designpattern.mvp.model.MVPApiService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 3.Retrofit 客户端封装。
 * OkHttp 在 Retrofit2 中已经内置作为默认的 HTTP 客户端，除非需要特殊配置，否则你不需要手动再添加 OkHttp 的代码。
 */
object MVPRetrofitClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val apiService: MVPApiService by lazy {
        Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(MVPApiService::class.java)
    }

}