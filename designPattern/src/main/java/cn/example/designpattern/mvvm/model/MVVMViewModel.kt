package cn.example.designpattern.mvvm.model

import cn.example.designpattern.mvp.http.HttpUtils
import cn.example.designpattern.mvp.http.RequestParam
import cn.example.designpattern.mvp.http.ResponseObserver
import cn.example.designpattern.mvp.model.MVPDataModel

class MVVMViewModel : BaseViewModel<MVPDataModel>() {

    fun fetchPostData(postId: Int) {
        // 设置加载状态为 true
        setLoading(true)

        val requestParam = RequestParam().apply {
            put("title", "fooT")
            put("body", "barT")
            put("userId", postId)
        }

        HttpUtils.post("posts", requestParam, object : ResponseObserver<MVPDataModel>() {
            override fun onSuccess(data: MVPDataModel) {
                // 更新数据
                setData(data)
                // 设置加载状态为 false
                setLoading(false)
            }

            override fun onFail(errorMessage: String?) {
                // 设置错误信息
                setErrorMessage(errorMessage)
                // 设置加载状态为 false
                setLoading(false)
            }
        })
    }
}
