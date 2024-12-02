package cn.example.designpattern.mvvm.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModelNormal : ViewModel() {

    // 加载状态
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    // 错误信息
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // 设置加载状态
    protected fun setLoading(isLoading: Boolean) {
        _loading.value = isLoading
    }

    // 设置错误信息
    protected fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }
}
