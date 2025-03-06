package cn.example.encrypte.sm2


import android.util.Base64
import cn.example.encrypte.utils.DataFormatUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

object SM2EncryptionUtils {

    // 默认字符编码和加密模式
    var charset = Charsets.UTF_8

    // 控制加密类型，1代表SM2，2代表SM2/NONE/NOPADDING
    var encryptionType = 2

    // 确保BouncyCastle提供者已加载
    init {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.addProvider(BouncyCastleProvider())
    }

    // 控制返回密钥的格式。 默认为true，表示16进制字符串。如果为false，输出Base64字符串
    var isHexFormat = true

    /**
     * 获取对应的Cipher实例
     *
     * Cipher.getInstance("SM2", "BC")
     *  模式：默认模式，通常是 ECB（电子密码本模式）。
     *  填充：默认填充方式，通常是 PKCS#7 填充（这也是默认的填充方式，适用于许多加密算法，如 RSA、AES 等）。
     *
     * Cipher.getInstance("SM2/NONE/NOPADDING", "BC")
     *  模式：NONE 表示没有使用任何加密模式，意味着它不进行分组加密。数据长度必须符合算法要求，否则加密操作会失败。
     *  填充：NOPADDING 表示不使用任何填充，要求输入数据的长度必须是算法块大小的整数倍。如果输入数据长度不符合要求，会导致加密失败或抛出异常。
     */
    private fun getCipher(): Cipher {
        val transformation = when (encryptionType) {
            1 -> "SM2"
            2 -> "SM2/NONE/NOPADDING"
            else -> throw IllegalArgumentException("不支持的加密类型")
        }

        // 返回对应的Cipher实例
        return Cipher.getInstance(transformation)
    }

    /**
     * 生成SM2密钥对（使用sm2p256v1椭圆曲线）
     * sm2p256v1是SM2公钥密码算法所使用的标准椭圆曲线（也称为国密曲线）。
     * 它是基于256位的素数域，意味着它使用的每个点的坐标是256位，具有较强的安全性。
     *
     * @return 公钥和私钥字符串（根据指定格式返回）。
     */
    fun generateSM2KeyPair(isHex: Boolean = true): Pair<String, String> {
        this.isHexFormat = isHex
        // 初始化KeyPairGenerator并设置曲线参数
        val keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC")
        // 使用sm2p256v1曲线
        val sm2p256v1 = ECGenParameterSpec("sm2p256v1")
        keyPairGenerator.initialize(sm2p256v1, SecureRandom())

        // 生成密钥对
        val keyPair: KeyPair = keyPairGenerator.generateKeyPair()

        // 获取公钥和私钥
        val publicKey: PublicKey = keyPair.public
        val privateKey: PrivateKey = keyPair.private

        // 根据输出格式决定编码方式
        val publicKeyString = if (isHexFormat) {
            // 转为16进制字符串
            Hex.toHexString(publicKey.encoded)
        } else {
            // 转为Base64字符串
            Base64.encodeToString(publicKey.encoded, Base64.NO_WRAP)
        }

        val privateKeyString = if (isHexFormat) {
            // 转为16进制字符串
            Hex.toHexString(privateKey.encoded)
        } else {
            // 转为Base64字符串
            Base64.encodeToString(privateKey.encoded, Base64.NO_WRAP)
        }

        // 返回公钥和私钥字符串
        return Pair(publicKeyString, privateKeyString)
    }

    /**
     * 从十六进制字符串或Base64解码获取SM2公钥（X.509 编码）
     * 默认通过十六进制字符串获取公钥，可以通过参数控制使用Base64格式
     *
     * @param key 传入的公钥字符串，可以是十六进制或Base64格式
     * @return 公钥
     */
    fun getPublicKeyFromKeyStr(key: String): PublicKey {
        val keyBytes = getStrBytes(key, isHexFormat)
        val keyFactory = KeyFactory.getInstance("EC", "BC")
        val spec = X509EncodedKeySpec(keyBytes)
        return keyFactory.generatePublic(spec)
    }

    /**
     * 从十六进制字符串或Base64解码获取SM2私钥（PKCS#8 编码）
     * 默认通过十六进制字符串获取私钥，可以通过参数控制使用Base64格式
     *
     * @param key 传入的私钥字符串，可以是十六进制或Base64格式
     * @return 私钥
     */
    fun getPrivateKeyFromKeyStr(key: String): PrivateKey {
        val keyBytes = getStrBytes(key, isHexFormat)
        val keyFactory = KeyFactory.getInstance("EC", "BC")
        val spec = PKCS8EncodedKeySpec(keyBytes)
        return keyFactory.generatePrivate(spec)
    }

    /**
     * 根据提供的格式（十六进制或Base64）解码密钥字符串，返回字节数组。
     *
     * @param key 需要解码的密钥字符串，可以是十六进制字符串或Base64字符串。
     * @param isHex 如果为true，则表示密钥字符串是十六进制格式；如果为false，则表示密钥字符串是Base64格式。
     * @return 解码后的字节数组。
     */
    private fun getStrBytes(key: String, isHex: Boolean): ByteArray? {
        // 返回解码后的字节数组
        return if (isHex) {
            // 十六进制解码
            Hex.decode(key)
        } else {
            // Base64解码
            Base64.decode(key, Base64.NO_WRAP)
        }
    }

    /**
     * SM2公钥加密，返回密文字符串
     */
    fun encrypt(publicKey: PublicKey, plainText: String, isHex: Boolean = true): String {
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedData = cipher.doFinal(plainText.toByteArray(charset))
        return if (isHex) {
            byteArrayToHexStr(encryptedData)
        } else {
            encodeByteArrayToBase64(encryptedData)
        }
    }

    /**
     * SM2私钥解密，返回原始明文字符串
     */
    fun decrypt(privateKey: PrivateKey, encryptedText: String, isHex: Boolean = true): String {
        val cipher = getCipher()
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        var strBytes = getStrBytes(encryptedText, isHex)
        val decryptedData = cipher.doFinal(strBytes)
        return String(decryptedData, charset)
    }

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

    // 将字节数组转为Base64编码的字符串。能够把任意字节数据转换为可打印的字符串。
    fun encodeByteArrayToBase64(byteArray: ByteArray): String {
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}