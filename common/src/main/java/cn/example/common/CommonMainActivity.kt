package cn.example.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class CommonMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_main)

        //如何实现
//        findViewById<TextView>(R.id.tvCommonToDesign).setOnClickListener {
//            startActivity(Intent(this, DesignPatternMainActivity::class.java))
//        }
    }
}