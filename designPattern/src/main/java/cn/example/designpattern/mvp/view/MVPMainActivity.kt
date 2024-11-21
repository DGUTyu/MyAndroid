package cn.example.designpattern.mvp.view


import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.example.designpattern.R
import cn.example.designpattern.mvp.model.MVPDataModel
import cn.example.designpattern.mvp.presenter.MVPMainPresenter


class MVPMainActivity : AppCompatActivity(), MVPMainView {
    private lateinit var presenter: MVPMainPresenter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mvp_activity_main)

        progressBar = findViewById(R.id.progressBar)
        presenter = MVPMainPresenter(this)
//        presenter.fetchAllPosts() // 获取所有文章
        presenter.fetchPostById(1) // 获取 ID 为 1 的文章
//        presenter.fetchPostsByUser(1) // 获取用户 ID 为 1 的文章
    }

    override fun showLoading() {
        progressBar.visibility = android.view.View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = android.view.View.GONE
    }

    override fun onGetPostsSuccess(posts: List<MVPDataModel>) {
        Toast.makeText(this, "Fetched ${posts.size} posts", Toast.LENGTH_LONG).show()
        // Populate RecyclerView or update UI with data
    }

    override fun onGetSinglePostSuccess(post: MVPDataModel) {
        Log.e("TAG", "onGetSinglePostSuccess: $post")
    }

    override fun onGetPostsError(error: String) {
        Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.clear()
    }
}
