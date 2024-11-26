package cn.example.designpattern.mvp.http

import com.google.gson.JsonObject
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.Response

abstract class ResponseObserver<T : Any> : Observer<Response<JsonObject>> {
    override fun onSubscribe(d: Disposable) {}

    override fun onNext(response: Response<JsonObject>) {
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                val parsedData = parseResponse(body)
                onSuccess(parsedData)
            } else {
                onError("Empty response body")
            }
        } else {
            onError("Response failed: ${response.message()}")
        }
    }

    override fun onError(e: Throwable) {
        onError(e.message ?: "Unknown error")
    }

    override fun onComplete() {
        // 可选：请求完成后的逻辑
    }


    // 解析 Response 数据回调
    abstract fun parseResponse(json: JsonObject): T

    // 成功回调
    abstract fun onSuccess(data: T)

    // 错误回调
    abstract fun onError(errorMessage: String)
}
