package cn.example.designpattern

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import cn.example.base.manager.RouterManager

class DesignPatternMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_design_pattern_main)

        findViewById<TextView>(R.id.tvDesignToCommon).setOnClickListener {
            RouterManager.getInstance().goCommonPage()
        }
    }
}