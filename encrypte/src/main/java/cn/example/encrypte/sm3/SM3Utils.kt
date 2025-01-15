package cn.example.encrypte.sm3

import org.bouncycastle.crypto.digests.SM3Digest
import java.nio.charset.StandardCharsets
import java.util.*

object SM3Utils {

    /**
     * 计算 SM3 摘要值
     * @param input 输入字符串
     * @param uppercase 是否返回大写字符串，默认 true
     * @return 摘要值的十六进制表示
     */
    fun getSign(input: String, uppercase: Boolean = true): String {
        val sm3Digest = SM3Digest()
        val inputBytes = input.toByteArray(StandardCharsets.UTF_8)
        sm3Digest.update(inputBytes, 0, inputBytes.size)

        val hash = ByteArray(sm3Digest.digestSize)
        sm3Digest.doFinal(hash, 0)
        return hash.toHexString(uppercase)
    }

    /**
     * 校验 SM3 签名值
     * @param input 输入字符串
     * @param expectedHash 预期的签名值（十六进制字符串）
     * @return 是否匹配
     */
    fun verifySign(input: String, expectedHash: String): Boolean {
        val calculatedHash = getSign(input)
        return calculatedHash.equals(expectedHash, ignoreCase = true)
    }

    /**
     * 扩展函数
     * 将字节数组转换为十六进制字符串
     * @param uppercase 是否返回大写字符串
     */
    private fun ByteArray.toHexString(uppercase: Boolean = true): String {
        val hexString = StringBuilder(this.size * 2)
        for (byte in this) {
            val hex = (byte.toInt() and 0xFF).toString(16)
            if (hex.length == 1) hexString.append("0")
            hexString.append(hex)
        }
        return if (uppercase) hexString.toString().toUpperCase(Locale.ROOT) else hexString.toString().toLowerCase(Locale.ROOT)
    }
}
