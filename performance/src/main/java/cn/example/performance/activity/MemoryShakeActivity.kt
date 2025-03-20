package cn.example.performance.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cn.example.performance.R

class MemoryShakeActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        @SuppressLint("HandlerLeak")
        private val mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                // 创造内存抖动
                repeat(101) {
                    val arg = Array(100000) { "" }
                }
                sendEmptyMessageDelayed(0, 30)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance_memory)
        findViewById<Button>(R.id.bt_memory).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        mHandler.sendEmptyMessage(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }
}
