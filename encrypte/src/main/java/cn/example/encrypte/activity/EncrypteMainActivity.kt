package cn.example.encrypte.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.example.encrypte.R
import cn.example.encrypte.sm4.SM4EncryptionUtils
import cn.example.encrypte.utils.DataFormatUtils

class EncrypteMainActivity : AppCompatActivity() {

    private lateinit var etPlainText: EditText
    private lateinit var etKey: EditText
    private lateinit var tvEncryptedText: TextView
    private lateinit var tvDecryptedText: TextView
    private lateinit var btnEncrypt: Button
    private lateinit var btnDecrypt: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encrypte_main)
        initView()
        initListener()
        initTestData()
    }

    private fun initView() {
        // Initialize views
        etPlainText = findViewById(R.id.et_plainText)
        etKey = findViewById(R.id.et_key)
        tvEncryptedText = findViewById(R.id.tv_encryptedText)
        tvDecryptedText = findViewById(R.id.tv_decryptedText)
        btnEncrypt = findViewById(R.id.btn_encrypt)
        btnDecrypt = findViewById(R.id.btn_decrypt)
    }

    private fun initListener() {
        // Encryption button click listener
        btnEncrypt.setOnClickListener {
            sm4Encrypt()
        }

        // Decryption button click listener
        btnDecrypt.setOnClickListener {
            sm4Decrypt()
        }
    }

    private fun initTestData() {
        initSM4TestData()
    }

    private fun initSM4TestData() {
        etPlainText.setText("dasdasdas1123456")
        etKey.setText("7533ACC1704814D8B228532FFE5F8616")
    }

    private fun sm4Encrypt() {
        val plainText = etPlainText.text.toString()
        val key = etKey.text.toString()
        val encryptedData: ByteArray = SM4EncryptionUtils.encrypt(plainText, key, true)
        val base64Encoded = DataFormatUtils.encodeByteArrayToBase64(encryptedData)
        val encryptedData2 = DataFormatUtils.decodeBase64ToByteArray(base64Encoded)
        val hexStr = DataFormatUtils.byteArrayToHexStr(encryptedData2)
        // 处理加密后的字节数组...
        Log.e("TAG", "encryptedData: $encryptedData")
        Log.e("TAG", "base64Encoded: $base64Encoded")
        Log.e("TAG", "encryptedData2: $encryptedData2")
        Log.e("TAG", "hexStr: $hexStr")
        tvEncryptedText.text = hexStr
    }

    private fun sm4Decrypt() {
        //从16进制字符串恢复加密结果并解密：
        val encryptedText = tvEncryptedText.text.toString()
        val key = etKey.text.toString()
        val encryptedData = DataFormatUtils.hexStrToByteArray(encryptedText)
        val decryptedData = SM4EncryptionUtils.decrypt(encryptedData, key, isKeyHex = true)
        val decryptedText = String(decryptedData, DataFormatUtils.charset)
        tvDecryptedText.text = decryptedText
    }
}
