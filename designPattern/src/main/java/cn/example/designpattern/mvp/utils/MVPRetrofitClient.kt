package cn.example.designpattern.mvp.utils

import cn.example.designpattern.BuildConfig
import cn.example.designpattern.mvp.model.MVPApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 3.Retrofit 客户端封装。
 * OkHttp 在 Retrofit2 中已经内置作为默认的 HTTP 客户端，除非需要特殊配置，否则你不需要手动再添加 OkHttp 的代码。
 * 何时需要自定义 OkHttp？
 * 如果你有以下需求，可以引入自定义的 OkHttpClient：
 * 添加拦截器（比如日志拦截器、认证拦截器等）。
 * 修改超时时间。
 * 管理缓存。
 * 自定义网络行为。
 */
object MVPRetrofitClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    // 自定义 OkHttpClient 和日志拦截器
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // 设置日志级别，BODY 包括请求和响应的头部及正文（如参数和 JSON 数据）
        level = HttpLoggingInterceptor.Level.BODY
        //HEADERS 输出内容：包括请求和响应的头部信息，但不包括正文内容
        //level = HttpLoggingInterceptor.Level.HEADERS
        //BASIC 输出内容：仅输出请求方法、URL、响应状态代码和响应头部信息，不包括请求和响应的正文。
        //level = HttpLoggingInterceptor.Level.BASIC
        //NONE 不输出任何日志信息
        //level = HttpLoggingInterceptor.Level.NONE
    }

    // 创建 OkHttpClient，并在 Debug 模式下添加日志拦截器
    private val okHttpClient = OkHttpClient.Builder().apply {
        // 设置超时时间
        connectTimeout(30, TimeUnit.SECONDS)
        readTimeout(30, TimeUnit.SECONDS)

        // 仅在 Debug 模式下添加日志拦截器
        if (BuildConfig.DEBUG) {
            addInterceptor(loggingInterceptor)
        }
    }.build()

    val apiService: MVPApiService by lazy {
        Retrofit.Builder()
                .baseUrl(BASE_URL)
                // 使用自定义的 OkHttpClient
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(MVPApiService::class.java)
    }

}