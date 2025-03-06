package cn.example.myandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import cn.example.base.manager.RouterManager
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

        //sm2 公钥hexStr 182char
        //3059301306072a8648ce3d020106082a811ccf5501822d03420004dddc144014c96aa02e42425ac48dc4426f5fe678e720754ca4b74b4eeb562f023f1e0f7911b783bc486e6e9b454b7f327f765d1d00650f50e44a3d12f2a58788
        //sm2 私钥hexStr 300char
        //308193020100301306072a8648ce3d020106082a811ccf5501822d0479307702010104203d2bb167a6b199febcbc83f7345df4c56bac45315302cd2f4e1fa429893fc555a00a06082a811ccf5501822da14403420004dddc144014c96aa02e42425ac48dc4426f5fe678e720754ca4b74b4eeb562f023f1e0f7911b783bc486e6e9b454b7f327f765d1d00650f50e44a3d12f2a58788
        //spay sm2公钥：182char
        //3059301306072A8648CE3D020106082A811CCF5501822D0342000479FF212EEC7E9361CF10B2027EA3221CCC15CC54AFED3D03005573C74AE28F2E7A522D57577279E480EBB6F4177E2FB32386C8B39BAC7DC574739D96BA1DE882
        RouterManager.getInstance().goEncryptePage()
    }
}