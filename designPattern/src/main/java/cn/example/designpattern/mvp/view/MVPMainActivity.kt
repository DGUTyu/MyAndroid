package cn.example.designpattern.mvp.view


import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.example.designpattern.R
import cn.example.designpattern.mvp.adapter.PostsAdapter
import cn.example.designpattern.mvp.model.MVPDataModel
import cn.example.designpattern.mvp.presenter.MVPMainPresenter


class MVPMainActivity : AppCompatActivity(), MVPMainView {
    private lateinit var presenter: MVPMainPresenter
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mvp_activity_main)
        initView()
        initListener()
    }

    private fun initView() {
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.mvpRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PostsAdapter(mutableListOf())
        recyclerView.adapter = adapter
    }

    private fun initListener() {
        presenter = MVPMainPresenter(this)
        findViewById<Button>(R.id.mvpBtnAll).setOnClickListener {
            presenter.fetchAllPosts() // 获取所有文章
        }

        findViewById<Button>(R.id.mvpBtnID).setOnClickListener {
            presenter.fetchPostById(1) // 获取 ID 为 1 的文章
        }

        findViewById<Button>(R.id.mvpBtnUser).setOnClickListener {
            presenter.fetchPostsByUser(1) // 获取用户 ID 为 1 的文章
        }
    }

    override fun showLoading() {
        progressBar.visibility = android.view.View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = android.view.View.GONE
    }

    override fun onGetPostsSuccess(posts: List<MVPDataModel>) {
        Log.e("TAG", "Fetched ${posts.size} posts")
        adapter.setNewData(posts)
    }

    override fun onGetSinglePostSuccess(post: MVPDataModel) {
        Log.e("TAG", "onGetSinglePostSuccess: $post")
        adapter.setNewData(listOf(post))
    }

    override fun onGetPostsError(error: String) {
        Log.e("TAG", "Error: $error")
        adapter.setNewData(mutableListOf())
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.clear()
    }
}
