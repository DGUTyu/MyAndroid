package cn.example.encrypte.utils


import android.util.Base64

object DataFormatUtils {
    // 配置项：默认字符编码和加密模式
    var charset = Charsets.UTF_8

    // 将字节数组转换为十六进制字符串。适用于任何字节数组
    fun byteArrayToHexStr(bytes: ByteArray, isUpperCase: Boolean = false): String {
        return bytes.joinToString("") {
            if (isUpperCase) {
                // 大写
                String.format("%02X", it)
            } else {
                // 小写
                String.format("%02x", it)
            }
        }
    }

    // 将十六进制字符串转换为字节数组
    fun hexStrToByteArray(hex: String): ByteArray {
        require(hex.length % 2 == 0) { "无效的十六进制字符串，长度必须为偶数" }
        require(hex.matches(Regex("[0-9a-fA-F]+"))) { "无效的十六进制字符串，必须只包含0-9和a-f/A-F的字符" }

        return ByteArray(hex.length / 2) { index ->
            hex.substring(index * 2, index * 2 + 2).toInt(16).toByte()
        }
    }

    // 将字节数组转为Base64编码的字符串。能够把任意字节数据转换为可打印的字符串。
    fun encodeByteArrayToBase64(byteArray: ByteArray): String {
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    // 将Base64字符串解码为字节数组
    fun decodeBase64ToByteArray(base64Data: String): ByteArray {
        return Base64.decode(base64Data, Base64.NO_WRAP)
    }

    // 将Base64字符串解码为原始字符串
    fun decodeBase64ToStr(base64Data: String): String {
        val decodedData = Base64.decode(base64Data, Base64.NO_WRAP)
        return String(decodedData, charset)
    }
}