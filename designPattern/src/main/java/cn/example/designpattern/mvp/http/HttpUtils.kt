package cn.example.designpattern.mvp.http

import cn.example.designpattern.mvp.model.JsonApiService
import cn.example.designpattern.mvp.utils.MVPRetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object HttpUtils {

    private val jsonApiService: JsonApiService = MVPRetrofitClient.jsonApiService

    // 泛型化的 POST 请求方法
    fun <T : Any> postRequest(
            url: String,
            requestParam: RequestParam,
            observer: ResponseObserver<T>
    ) {
        jsonApiService.sendPostRequest(url, requestParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }
}