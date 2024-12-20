package cn.example.designpattern.mvp.view


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.example.designpattern.R
import cn.example.designpattern.mvp.adapter.PostsAdapter
import cn.example.designpattern.mvp.http.HttpUtils
import cn.example.designpattern.mvp.http.RequestParam
import cn.example.designpattern.mvp.http.ResponseObserver
import cn.example.designpattern.mvp.model.MVPDataModel
import cn.example.designpattern.mvp.presenter.MVPMainPresenter
import cn.example.designpattern.mvvm.model.MVVMViewModel
import cn.example.designpattern.mvvm.model.MVVMViewModelNormal


class MVPMainActivity : AppCompatActivity(), MVPMainView {
    private lateinit var presenter: MVPMainPresenter
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostsAdapter

    private lateinit var viewModel: MVVMViewModel
    //private lateinit var viewModel: MVVMViewModelNormal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mvp_activity_main)
        initView()
        initListener()
        mvvmDemo()
    }

    private fun mvvmDemo() {
        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(MVVMViewModel::class.java)
        //viewModel = ViewModelProvider(this).get(MVVMViewModelNormal::class.java)

        // Observe LiveData
        viewModel.data.observe(this, Observer { data ->
            // Update UI with the data
            data?.let { onGetSinglePostSuccess(it) }
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            // Show/hide loading indicator
            findViewById<ProgressBar>(R.id.progressBar).visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.errorMessage.observe(this, Observer { message ->
            // Show error message
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })

        // Fetch data (example: postId = 2)
        viewModel.fetchPostData(2)
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
            //presenter.fetchPostById(1) // 获取 ID 为 1 的文章
            sendPostRequest2()
        }

        findViewById<Button>(R.id.mvpBtnUser).setOnClickListener {
            //presenter.fetchPostsByUser(1) // 获取用户 ID 为 1 的文章
            presenter.sendPostRequest(getParam())
        }
    }

    private fun sendPostRequest2() {
        // 假设我们要请求的 URL 和参数
        val url = "https://jsonplaceholder.typicode.com/posts"
        val params = RequestParam().apply {
            put("title", "foo")
            put("body", "bar")
            put("userId", 1)
        }
        showLoading()
        // 调用 HttpUtils 进行网络请求
        HttpUtils.post(url, params, object : ResponseObserver<MVPDataModel>() {
            override fun onSuccess(data: MVPDataModel) {
                // 成功回调
                // 处理成功的返回数据
                hideLoading()
                Log.d("MyActivity", "Request succeeded with data: $data")
                onGetSinglePostSuccess(data)
            }

            override fun onFail(errorMessage: String?) {
                hideLoading()
                Log.e("MyActivity", "Request failed with error: $errorMessage")
            }
        })
    }

    private fun getParam(): RequestParam {
        // 准备测试数据
        val dataMap = mapOf(
                "title" to "foo",
                "body" to "bar",
                "userId" to "1"
        )

        return RequestParam().apply {
            put(dataMap)
//            addInputData("number", "123456")
//            addInputData(dataMap)
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
