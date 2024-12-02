package cn.example.designpattern.mvvm.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel<T> : ViewModel() {

    // 加载状态
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    // 错误信息
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // 数据
    private val _data = MutableLiveData<T>()
    val data: LiveData<T> get() = _data

    // 设置加载状态
    protected fun setLoading(isLoading: Boolean) {
        _loading.value = isLoading
    }

    // 设置错误信息
    protected fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    // 设置数据
    protected fun setData(data: T) {
        _data.value = data
    }

    //尽量在需要异步线程更新时才使用 postValue，否则优先使用 setValue，保持数据更新的时效性和可预测性
    protected fun postLoading(isLoading: Boolean) {
        _loading.postValue(isLoading)
    }

    protected fun postErrorMessage(message: String?) {
        _errorMessage.postValue(message)
    }

    protected fun postData(data: T) {
        _data.postValue(data)
    }

    //重置状态
    protected fun clearData() {
        _loading.value = false
        _errorMessage.value = null
        _data.value = null
    }
}

