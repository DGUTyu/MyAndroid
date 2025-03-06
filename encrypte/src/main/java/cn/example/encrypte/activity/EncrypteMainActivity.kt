package cn.example.encrypte.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.example.encrypte.R
import cn.example.encrypte.sm2.SM2CipherText
import cn.example.encrypte.sm2.SM2EncryptionUtils
import cn.example.encrypte.sm4.SM4EncryptionUtils
import cn.example.encrypte.utils.DataFormatUtils
import java.security.PrivateKey
import java.security.PublicKey

class EncrypteMainActivity : AppCompatActivity() {

    private lateinit var etPlainText: EditText
    private lateinit var etKey: EditText
    private lateinit var etKey2: EditText
    private lateinit var tvEncryptedText: TextView
    private lateinit var tvDecryptedText: TextView
    private lateinit var btnEncrypt: Button
    private lateinit var btnDecrypt: Button

    // 成员变量保存公私钥
    private lateinit var mPublicKey: String
    private lateinit var mPrivateKey: String

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
        etKey2 = findViewById(R.id.et_key2)
        tvEncryptedText = findViewById(R.id.tv_encryptedText)
        tvDecryptedText = findViewById(R.id.tv_decryptedText)
        btnEncrypt = findViewById(R.id.btn_encrypt)
        btnDecrypt = findViewById(R.id.btn_decrypt)
    }

    private fun initListener() {
        // Encryption button click listener
        btnEncrypt.setOnClickListener {
            //sm4Encrypt()
            sm2Encrypt()
        }

        // Decryption button click listener
        btnDecrypt.setOnClickListener {
            //sm4Decrypt()
            sm2Decrypt()
        }
    }

    private fun initTestData() {
        //initSM4TestData()
        initSM2TestData()
    }

    private fun initSM4TestData() {
        etPlainText.setText("dasdasdas1123456")
        etKey.setText("7533ACC1704814D8B228532FFE5F8616")
    }

    private fun initSM2TestData() {
        etPlainText.setText("这是要加密的明文")
        // 生成SM2公私钥对（Base64编码）
        val (publicKey, privateKey) = SM2EncryptionUtils.generateSM2KeyPair()
        // 将公私钥赋值给成员变量
        mPublicKey = publicKey
        mPrivateKey = privateKey
        Log.e("TAG", "Public Key: $publicKey")
        Log.e("TAG", "Private Key: $privateKey")
        etKey.setText(mPublicKey)
        etKey2.setText(mPrivateKey)
        testSM2CipherText()
    }

    private fun testSM2CipherText() {
        var ss = "043587E3956B90316D781ECC8D98127903B060AC8E284F49504D09BB28996230976F48D03A73E2206E75DD0728E582867081152255BC9FDA2C11C95920B83151E3C146916EB5B8899F08C49355644A3B2CB1E8F083667296055D93E66475011B3300"
        Log.e("TAG", "ss: $ss")
        var to132 = SM2CipherText().to132(ss)
        Log.e("TAG", "to132: $to132")
        var to123 = SM2CipherText().to123(to132)
        Log.e("TAG", "to123: $to123")

        var sss = "306A02201CA5F15F1EA0A45B0DA887C90DF18C0668A81A2AC58C6DE858740282F285CD02022100FDEE9BB93D5BBC44C2AAD0B537642489BADAD066BDBBF159F5E69D98877B9F010420DE8F910D7655D27D735ABAE3AAFC9D188248E1CFAC1809F9D193F2943ECCB3BD0401B7"
        Log.e("TAG", "sss: $sss")
        var d2iSM2CipherText = SM2CipherText().d2iSM2CipherText(sss)
        Log.e("TAG", "d2iSM2CipherText:" + d2iSM2CipherText)
        var i2dSM2CipherText = SM2CipherText().i2dSM2CipherText(d2iSM2CipherText)
        Log.e("TAG", "i2dSM2CipherText:" + i2dSM2CipherText)
    }


    private fun sm2Encrypt() {
        val plainText = etPlainText.text.toString()
        DataFormatUtils.encodeByteArrayToBase64(DataFormatUtils.hexStrToByteArray("3059301306072a8648ce3d020106082a811ccf5501822d0342000480f47f50e92250c7312e0fb22a074b08ff77bb46cea1fd66aeec61d026c4fe213e12b2ca03701abfabf97495602a4e5300594c23e8f716a38db15f615d3e2f6c"))
        val publicKey: PublicKey = SM2EncryptionUtils.getPublicKeyFromKeyStr(etKey.text.toString())
        val encryptedData = SM2EncryptionUtils.encrypt(publicKey, plainText)
        tvEncryptedText.text = encryptedData
    }

    private fun sm2Decrypt() {
        val encryptedText = tvEncryptedText.text.toString()
        val privateKey: PrivateKey = SM2EncryptionUtils.getPrivateKeyFromKeyStr(etKey2.text.toString())
        val decryptedData = SM2EncryptionUtils.decrypt(privateKey, encryptedText)
        tvDecryptedText.text = decryptedData
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
