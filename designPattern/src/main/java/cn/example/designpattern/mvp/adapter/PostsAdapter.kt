package cn.example.designpattern.mvp.adapter

import android.widget.TextView
import cn.example.designpattern.R
import cn.example.designpattern.mvp.model.MVPDataModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class PostsAdapter(data: List<MVPDataModel>) : BaseQuickAdapter<MVPDataModel, BaseViewHolder>(R.layout.mvp_item_post, data) {
    override fun convert(helper: BaseViewHolder, item: MVPDataModel?) {
        val postId = helper.getView<TextView>(R.id.postId)
        val postTitle = helper.getView<TextView>(R.id.postTitle)
        val postBody = helper.getView<TextView>(R.id.postBody)

        if (item != null) {
            postId.text = item.id.toString()
            postTitle.text = item.title
            postBody.text = item.body
        }
    }
}