package cn.example.designpattern.mvvm.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.example.designpattern.mvp.http.HttpUtils
import cn.example.designpattern.mvp.http.RequestParam
import cn.example.designpattern.mvp.http.ResponseObserver
import cn.example.designpattern.mvp.model.MVPDataModel

class MVVMViewModelNormal : BaseViewModelNormal() {

    private val _data = MutableLiveData<MVPDataModel>()
    val data: LiveData<MVPDataModel> get() = _data

    fun fetchPostData(postId: Int) {
        // 设置加载状态为 true
        setLoading(true)

        val requestParam = RequestParam().apply {
            put("title", "fooN")
            put("body", "barN")
            put("userId", postId)
        }

        HttpUtils.post("posts", requestParam, object : ResponseObserver<MVPDataModel>() {
            override fun onSuccess(data: MVPDataModel) {
                // 更新数据
                _data.value = data
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

