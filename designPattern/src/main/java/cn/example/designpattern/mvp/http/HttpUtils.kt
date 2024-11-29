package cn.example.designpattern.mvp.http

import cn.example.designpattern.mvp.model.JsonApiService
import cn.example.designpattern.mvp.utils.CryptoDemoUtil
import cn.example.designpattern.mvp.utils.MVPRetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody

object HttpUtils {
    private val mediaType = MediaType.parse("application/json;charset=utf-8")

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

    // 泛型化的 POST 请求方法
    fun <T : Any> post(
            url: String,
            requestParam: RequestParam,
            observer: ResponseObserver<T>
    ) {
        observer.setUrl(url)
        var SM4_A = System.currentTimeMillis().toString()
        observer.setSecretKey("SM4_A：" + SM4_A)
        //获得加密加签字符串
        val requestParamString: String = CryptoDemoUtil.addData(requestParam, SM4_A)
        var body = RequestBody.create(mediaType, requestParamString)
        jsonApiService.httpPost(url, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }
}