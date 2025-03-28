package cn.example.performance.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.example.performance.R

class PerformanceMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_performance_main)
        findViewById<TextView>(R.id.tv).setOnClickListener {
            startActivity(Intent(this, MemoryLeakActivity::class.java))
        }
    }
}