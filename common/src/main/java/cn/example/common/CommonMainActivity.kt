package cn.example.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import cn.example.common.manager.RouterManager

class CommonMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_main)

        //如何实现
        findViewById<TextView>(R.id.tvCommonToDesign).setOnClickListener {
            RouterManager.getInstance().goDesignPatternPage()
        }
    }
}