package cn.example.designpattern.mvp.model

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 1.定义API接口
 *
 * @GET 表示使用 GET 方法来请求数据。
 * "posts" 是一个相对路径，表示请求的具体资源地址。完整的 URL 就是：https://jsonplaceholder.typicode.com/posts
 * Observable<List<DataModel>> 表示服务器响应的数据将被解析为一个包含 DataModel 对象的列表
 * 动态路径参数:当调用 getPostById(1) 时，请求的 URL 会变成：https://jsonplaceholder.typicode.com/posts/1
 * 查询参数:调用时apiService.getPostsByUser(1)时，会生成请求：https://jsonplaceholder.typicode.com/posts?userId=1
 */
interface MVPApiService {
    @GET("posts") // 示例接口
    fun getPosts(): Observable<List<MVPDataModel>>

    @GET("posts/{id}")
    fun getPostById(@Path("id") postId: Int): Observable<MVPDataModel>

    @GET("posts")
    fun getPostsByUser(@Query("userId") userId: Int): Observable<List<MVPDataModel>>
}