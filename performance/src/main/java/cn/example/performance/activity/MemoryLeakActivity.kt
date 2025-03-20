package cn.example.performance.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import cn.example.performance.CallBack
import cn.example.performance.CallBackManager
import cn.example.performance.R

class MemoryLeakActivity : AppCompatActivity(), CallBack {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance_memoryleak)
        val imageView = findViewById<ImageView>(R.id.iv_memoryleak)
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.splash)
        imageView.setImageBitmap(bitmap)

        CallBackManager.addCallBack(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        CallBackManager.removeCallBack(this)
    }

    override fun dpOperate() {
        // do sth
    }
}