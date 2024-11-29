package cn.example.designpattern.mvp.model

import cn.example.designpattern.mvp.http.RequestParam
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface JsonApiService {
    @POST
    fun sendPostRequest(
            @Url url: String,
            @Body requestParam: RequestParam
    ): Observable<Response<JsonObject>>

    @POST
    fun httpPost(
            @Url url: String,
            @Body body: RequestBody
    ): Observable<Response<JsonObject>>
}
