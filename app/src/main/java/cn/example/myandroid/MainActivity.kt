package cn.example.myandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import cn.example.common.CommonMainActivity
import cn.example.designpattern.DesignPatternMainActivity
import cn.example.designpattern.mvp.view.MVPMainActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.tvToCommon).setOnClickListener {
            startActivity(Intent(this, CommonMainActivity::class.java))
        }

        findViewById<TextView>(R.id.tvToDesignPattern).setOnClickListener {
            startActivity(Intent(this, MVPMainActivity::class.java))
        }
    }
}