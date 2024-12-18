package cn.example.encrypte.sm4


import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.SecureRandom
import java.security.Security
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object SM4EncryptionUtils {
    //算法名称
    private const val ALGORITHM = "SM4"

    // 是否启用密钥填充、截断(要求密钥长度为16字节)
    var keyPaddingEnabled = true

    // 默认字符编码和加密模式
    var charset = Charsets.UTF_8

    // 默认SM4/CBC/NoPadding
    var encryptionType = 2

    // 存储IV的成员变量
    private var currentIV: ByteArray? = null

    // 初始化BouncyCastle提供器，确保SM4算法支持
    init {
        // 如果使用的是标准的Java运行环境，且SM4算法依赖BouncyCastle提供的安全服务，则需要添加
        // 通过显式地移除并重新添加 BouncyCastle 提供者，确保了使用的是正确的加密实现，从而解决了算法找不到的问题。
        // 这通常是因为 JVM 或类加载器缓存了旧的提供者实例，而在更新库后需要刷新注册的提供者。
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.addProvider(BouncyCastleProvider())
    }

    // 获取Cipher实例，根据配置的加密模式选择对应的加密算法
    private fun getCipher(): Cipher {
        val transformation = when (encryptionType) {
            1 -> {
                // ECB（电子密码本）模式：每次加密一个块（block），块的大小通常为128位（16字节）。
                // 由于没有链接性，每个加密块是独立的，容易受到攻击，通常不建议用于加密大量数据。
                // PKCS7Padding：如果数据块大小不符合算法要求，则通过填充字节的方式填充数据至完整的块大小。
                // 适用于加密较小数据（例如密钥、密码等）。
                "SM4/ECB/PKCS7Padding"
            }

            2 -> {
                // CBC（密码分组链接）模式：加密每个数据块时使用前一个块的密文作为初始化向量（IV），
                // 增加了加密的安全性。每个数据块的加密不再是独立的，因此安全性更高。
                // NoPadding：没有填充，数据长度必须是算法块大小的整数倍。如果数据不足，需要手动填充。
                // 适用于需要保证高安全性和数据长度可控的场景。
                "SM4/CBC/NoPadding"
            }

            3 -> {
                // ECB（电子密码本）模式：每个数据块独立加密，适用于没有长度限制的场景。
                // NoPadding：没有填充，数据长度必须是算法块大小的整数倍。如果数据不足，需要手动填充。
                // 适用于固定长度的加密数据，如加密文件的块。
                "SM4/ECB/NoPadding"
            }

            else -> throw IllegalArgumentException("不支持的加密类型")
        }

        // 返回对应的Cipher实例
        return Cipher.getInstance(transformation)
    }


    // 加密字符串，返回加密字节数组
    fun encrypt(data: String, key: String, isKeyHex: Boolean = false): ByteArray {
        var inputData = data.toByteArray(charset)
        val cipher = getCipher()

        // 对于 `SM4/CBC/NoPadding` 和 `SM4/ECB/NoPadding` 都需要填充
        if (encryptionType == 2 || encryptionType == 3) {
            inputData = pkcs7PadData(inputData)
        }

        // 根据加密模式选择初始化方式
        when (encryptionType) {
            2 -> { // CBC模式
                // 生成16字节随机IV, 将IV存储起来
                val iv = ByteArray(16)
                SecureRandom().nextBytes(iv)
                currentIV = iv
                val ivSpec = IvParameterSpec(iv)
                cipher.init(Cipher.ENCRYPT_MODE, getKey(key, isKeyHex), ivSpec)
            }
            3, 1 -> { // ECB模式不需要IV，且1与3模式只需要设置密钥
                cipher.init(Cipher.ENCRYPT_MODE, getKey(key, isKeyHex))
            }
            else -> throw IllegalArgumentException("不支持的加密类型")
        }

        return cipher.doFinal(inputData)
    }

    // 解密字节数组，返回解密字节数组
    fun decrypt(data: ByteArray, key: String, isKeyHex: Boolean = false): ByteArray {
        val cipher = getCipher()

        // 根据加密模式选择初始化方式
        when (encryptionType) {
            2 -> { // CBC模式
                // 获取存储的IV，如果没有就用默认的IV（可以是空的或者全0）
                // 默认使用全0作为IV
                val iv = currentIV ?: ByteArray(16)
                val ivSpec = IvParameterSpec(iv)
                cipher.init(Cipher.DECRYPT_MODE, getKey(key, isKeyHex), ivSpec)
            }
            3, 1 -> { // ECB模式不需要IV，且1与3模式只需要设置密钥
                cipher.init(Cipher.DECRYPT_MODE, getKey(key, isKeyHex))
            }
            else -> throw IllegalArgumentException("不支持的加密类型")
        }

        val decryptedData = cipher.doFinal(data)

        // 对于 `SM4/CBC/NoPadding` 和 `SM4/ECB/NoPadding` 都需要去填充
        if (encryptionType == 2 || encryptionType == 3) {
            return pkcs7UnpadData(decryptedData)
        }

        return decryptedData
    }

    // 获取密钥，要求密钥长度为16字节，支持16进制和普通字符串
    private fun getKey(key: String, isHex: Boolean): SecretKey {
        val keyBytes = if (isHex) {
            //DataFormatUtils.hexStrToByteArray(key)
            hexStrToByteArray(key)
        } else {
            key.toByteArray(charset)
        }

        // 如果启用了密钥填充，填充到16字节
        val paddedKey = if (keyPaddingEnabled) {
            // Kotlin 标准库中 ByteArray 的一个扩展方法，返回一个新的字节数组，并将原数组复制到这个新数组中。
            // 如果密钥长度不足16字节，用0填充；如果超过16字节，则截断
            keyBytes.copyOf(16)
        } else {
            // 如果未启用填充，确保密钥长度为16字节
            require(keyBytes.size == 16) { "密钥长度必须为16字节" }
            // 返回原始密钥字节数组
            keyBytes
        }

        // 使用填充后的密钥字节数组，生成SM4密钥
        return SecretKeySpec(paddedKey, ALGORITHM)
    }

    // 将十六进制字符串转换为字节数组
    fun hexStrToByteArray(hex: String): ByteArray {
        require(hex.length % 2 == 0) { "无效的十六进制字符串，长度必须为偶数" }
        require(hex.matches(Regex("[0-9a-fA-F]+"))) { "无效的十六进制字符串，必须只包含0-9和a-f/A-F的字符" }

        return ByteArray(hex.length / 2) { index ->
            hex.substring(index * 2, index * 2 + 2).toInt(16).toByte()
        }
    }

    /**
     * PKCS7（Public Key Cryptography Standards #7）是一种标准的填充算法，广泛用于加密算法（尤其是对称加密，如AES、SM4等）的数据块填充。
     * 该算法的目的是确保加密的数据能够符合加密算法的要求，通常要求数据的长度是加密块大小的倍数（例如 128 位或 16 字节）。
     * 如果数据的长度不是加密块的倍数，则需要填充。
     * 填充规则：
     * 填充的字节数会等于数据缺少的字节数。比如，如果数据的长度是 14 字节，而块大小是 16 字节，则需要填充 2 字节。
     * 填充的字节内容会是填充长度的字节值。例如，如果需要填充 2 字节，则填充字节的值为 0x02。
     * 如果数据本来就是16字节的倍数，PKCS7 仍然会按照规范填充 16 个字节，填充字节的值为 0x10。
     */
    fun pkcs7PadData(data: ByteArray): ByteArray {
        val paddingLength = 16 - (data.size % 16)

        // 如果数据已经是16字节的倍数，按PKCS7规范需要填充16个字节，填充字节值为0x10
        if (paddingLength == 0) {
            return data + ByteArray(16) { 0x10.toByte() }
        }

        // 否则填充所需的字节数，填充字节的值为paddingLength
        val padding = ByteArray(paddingLength) { paddingLength.toByte() }

        return data + padding
    }


    fun pkcs7UnpadData(data: ByteArray): ByteArray {
        // 获取最后一个字节的值，即填充的字节数
        val paddingLength = data[data.size - 1].toInt()

        // 检查填充长度是否有效，必须在1到16之间。如果无效，则不进行去除填充
        if (paddingLength < 1 || paddingLength > 16) {
            return data
        }

        // 移除填充字节
        return data.copyOfRange(0, data.size - paddingLength)
    }

    /**
     * ISO 10126 填充方式与 PKCS7 填充方式类似，但存在一些区别。
     * ISO 10126 填充方式的关键点是：
     * 填充字节值： 在 ISO 10126 填充中，填充字节是随机生成的，而不是固定为填充长度。这是与 PKCS7 的最大区别。
     * 填充的字节数： 和 PKCS7 填充一样，填充的字节数也是 16 - (data.size % 16)。
     */
    fun iso10126PadData(data: ByteArray): ByteArray {
        val paddingLength = 16 - (data.size % 16)

        // 如果数据已经是16字节的倍数，ISO 10126要求填充16个随机字节
        if (paddingLength == 0) {
            val padding = ByteArray(16)
            // 填充随机字节
            SecureRandom().nextBytes(padding)
            // 最后一个字节是填充长度
            padding[15] = 16.toByte()
            return data + padding
        }

        // 否则填充所需的字节数，填充字节的值是随机的，最后一个字节是填充长度
        val padding = ByteArray(paddingLength)
        SecureRandom().nextBytes(padding)
        // 最后一个字节是填充长度
        padding[paddingLength - 1] = paddingLength.toByte()

        return data + padding
    }

    fun iso10126UnpadData(data: ByteArray): ByteArray {
        // 获取最后一个字节的值，即填充的字节数
        val paddingLength = data[data.size - 1].toInt()

        // 检查填充长度是否有效，必须在1到16之间。如果无效，则不进行去除填充
        if (paddingLength < 1 || paddingLength > 16) {
            return data
        }

        // 移除填充字节
        return data.copyOfRange(0, data.size - paddingLength)
    }

}