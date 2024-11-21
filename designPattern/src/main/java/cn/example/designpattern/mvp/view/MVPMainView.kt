package cn.example.designpattern.mvp.view

import cn.example.designpattern.mvp.model.MVPDataModel

/**
 * 4.定义 View 接口
 */
interface MVPMainView {
    fun showLoading()
    fun hideLoading()
    fun onGetPostsSuccess(posts: List<MVPDataModel>)
    fun onGetSinglePostSuccess(post: MVPDataModel)
    fun onGetPostsError(error: String)
}