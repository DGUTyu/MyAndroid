package cn.example.designpattern.mvp.presenter

import cn.example.designpattern.mvp.model.MVPDataModel
import cn.example.designpattern.mvp.utils.MVPRetrofitClient
import cn.example.designpattern.mvp.view.MVPMainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * 5.定义 Presenter
 */
class MVPMainPresenter(private val mainView: MVPMainView) {
    private val disposables = CompositeDisposable()

    // 获取所有文章
    fun fetchAllPosts() {
        mainView.showLoading()
        val disposable = MVPRetrofitClient.apiService.getPosts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { posts: List<MVPDataModel> ->
                            mainView.hideLoading()
                            mainView.onGetPostsSuccess(posts)
                        },
                        { error: Throwable ->
                            mainView.hideLoading()
                            mainView.onGetPostsError(error.message ?: "Unknown error")
                        }
                )
        disposables.add(disposable)
    }

    // 根据ID获取单个文章
    fun fetchPostById(postId: Int) {
        mainView.showLoading()
        val disposable = MVPRetrofitClient.apiService.getPostById(postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { post: MVPDataModel ->
                            mainView.hideLoading()
                            mainView.onGetSinglePostSuccess(post)
                        },
                        { error: Throwable ->
                            mainView.hideLoading()
                            mainView.onGetPostsError(error.message ?: "Unknown error")
                        }
                )
        disposables.add(disposable)
    }

    // 根据用户ID获取文章
    fun fetchPostsByUser(userId: Int) {
        mainView.showLoading()
        val disposable = MVPRetrofitClient.apiService.getPostsByUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { posts: List<MVPDataModel> ->
                            mainView.hideLoading()
                            mainView.onGetPostsSuccess(posts)
                        },
                        { error: Throwable ->
                            mainView.hideLoading()
                            mainView.onGetPostsError(error.message ?: "Unknown error")
                        }
                )
        disposables.add(disposable)
    }

    // 清理订阅
    fun clear() {
        disposables.clear()
    }
}
