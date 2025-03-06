package cn.example.encrypte.sm2

class SM2CipherText {

    /**
     * 转换原始数据为标准格式 (DER 编码，ASN.1 SEQUENCE)
     */
    fun i2dSM2CipherText(primalData: String): String {
        val sm2Sequence = SM2Sequence()
        var data = primalData

        sm2Sequence.C1x = data.substring(0, 64)
        data = data.substring(64)
        sm2Sequence.C1y = data.substring(0, 64)
        data = data.substring(64)
        sm2Sequence.C3 = data.substring(0, 64)
        data = data.substring(64)
        sm2Sequence.C2 = data

        val C1xTitle = if (sm2Sequence.C1x.substring(0, 2).toInt(16) > 127) "022100" else "0220"
        val C1yTitle = if (sm2Sequence.C1y.substring(0, 2).toInt(16) > 127) "022100" else "0220"
        val C3Title = "0420"
        val C2Title = "04" + genLenHex(sm2Sequence.C2)

        val sequenceMessage = C1xTitle + sm2Sequence.C1x +
                C1yTitle + sm2Sequence.C1y +
                C3Title + sm2Sequence.C3 +
                C2Title + sm2Sequence.C2

        val sequenceLenHex = genLenHex(sequenceMessage)
        return "30$sequenceLenHex$sequenceMessage"
    }

    /**
     * 将标准格式 (ASN.1 SEQUENCE) 转换为原始数据
     */
    fun d2iSM2CipherText(standardData: String): String {
        var message = standardData
        if (!message.startsWith(ASN1Util.SEQUENCE)) {
            ciphertextErr()
        }

        message = message.substring(ASN1Util.SEQUENCE.length)
        val sequenceLenHex = getLenHex(message)
        message = message.substring(sequenceLenHex.length)
        val sequenceLen = lenHex2Number(sequenceLenHex)

        if (sequenceLen != message.length / 2) {
            ciphertextErr()
        }

        val sm2Sequence = SM2Sequence()
        var tempMessage = readC1(sm2Sequence, message)
        tempMessage = readC3(sm2Sequence, tempMessage)
        tempMessage = readC2(sm2Sequence, tempMessage)

        println(sm2Sequence.toString())
        return sm2Sequence.C1x + sm2Sequence.C1y + sm2Sequence.C3 + sm2Sequence.C2
    }

    /**
     * 生成长度的十六进制表示
     */
    fun genLenHex(content: String): String {
        val size = content.length / 2
        var lenHex = size.toString(16)
        if (lenHex.length % 2 == 1) {
            lenHex = "0$lenHex"
        }
        return if (size < 0x80) {
            lenHex
        } else {
            val lenHexSize = lenHex.length / 2
            (lenHexSize or 0x80).toString(16) + lenHex
        }
    }

    /**
     * 提取数据长度的十六进制表示
     */
    fun getLenHex(data: String): String {
        val byte = data.substring(0, 2).toInt(16)
        val lenSize = if (byte > 127) byte - 0x80 + 1 else 1
        return data.substring(0, lenSize * 2)
    }

    /**
     * 将长度的十六进制表示转为数字
     */
    fun lenHex2Number(lenHex: String): Int {
        return if (lenHex.length == 2) {
            lenHex.toInt(16)
        } else {
            lenHex.substring(2).toInt(16)
        }
    }

    /**
     * 处理密文格式错误
     */
    fun ciphertextErr() {
        throw IllegalArgumentException("SM2 ciphertext error!")
    }

    /**
     * 读取 C1 部分
     */
    fun readC1(sm2Sequence: SM2Sequence, data: String): String {
        var tempData = data
        val xy = mutableListOf<String>()
        repeat(2) {
            when {
                tempData.startsWith("0220") -> {
                    xy.add(tempData.substring(4, 68))
                    tempData = tempData.substring(68)
                }
                tempData.startsWith("022100") -> {
                    xy.add(tempData.substring(6, 70))
                    tempData = tempData.substring(70)
                }
                else -> ciphertextErr()
            }
        }
        sm2Sequence.C1x = xy[0]
        sm2Sequence.C1y = xy[1]
        return tempData
    }

    /**
     * 读取 C2 部分
     */
    fun readC2(sm2Sequence: SM2Sequence, data: String): String {
        var tempData = data
        if (tempData.startsWith(ASN1Util.OCTET_STRING)) {
            tempData = tempData.substring(ASN1Util.OCTET_STRING.length)
            val C2LenHex = getLenHex(tempData)
            tempData = tempData.substring(C2LenHex.length)
            if (lenHex2Number(C2LenHex) != tempData.length / 2) {
                ciphertextErr()
            }
            sm2Sequence.C2 = tempData
        } else {
            ciphertextErr()
        }
        return tempData
    }

    /**
     * 读取 C3 部分
     */
    fun readC3(sm2Sequence: SM2Sequence, data: String): String {
        var tempData = data
        if (tempData.startsWith("0420")) {
            sm2Sequence.C3 = tempData.substring(4, 68)
            tempData = tempData.substring(68)
        } else {
            ciphertextErr()
        }
        return tempData
    }

    fun to123(standardData: String, addPrefix: Boolean = true): String {
        var message = standardData

        // 检查数据是否以 SEQUENCE 开头
        if (!message.startsWith(ASN1Util.SEQUENCE)) {
            ciphertextErr()
        }

        // 去掉 SEQUENCE 标志
        message = message.substring(ASN1Util.SEQUENCE.length)

        // 获取 SEQUENCE 长度并校验
        val sequenceLenHex = getLenHex(message)
        message = message.substring(sequenceLenHex.length)
        val sequenceLen = lenHex2Number(sequenceLenHex)
        if (sequenceLen != message.length / 2) {
            ciphertextErr()
        }

        // 初始化 SM2 序列
        val sm2Sequence = SM2Sequence()

        // 读取 C1、C3 和 C2
        message = readC1(sm2Sequence, message)
        message = readC3(sm2Sequence, message)
        message = readC2(sm2Sequence, message)

        // 输出调试信息
        println(sm2Sequence.toString())

        // 拼接原始数据
        var primalData = sm2Sequence.C1x + sm2Sequence.C1y + sm2Sequence.C2 + sm2Sequence.C3

        // 根据参数决定是否添加前缀 "04"
        if (addPrefix) {
            primalData = "04$primalData"
        }

        println("to123 secretKey: $primalData")
        return primalData
    }

    fun to132(primalData: String, removePrefix: Boolean = true): String {
        var data = primalData

        // 如果需要移除前缀 "04"，且数据以 "04" 开头
        if (removePrefix && data.startsWith("04")) {
            data = data.substring(2)
        }

        // 初始化 SM2 密文序列
        val sm2Sequence = SM2Sequence()

        // 提取 C1x, C1y, C2 和 C3
        sm2Sequence.C1x = data.substring(0, 64) // 提取 C1x（64 字符）
        data = data.substring(64)
        sm2Sequence.C1y = data.substring(0, 64) // 提取 C1y（64 字符）
        data = data.substring(64)
        sm2Sequence.C2 = data.substring(0, data.length - 64) // 提取 C2（剩余数据 - 64）
        sm2Sequence.C3 = data.substring(data.length - 64) // 提取 C3（最后 64 字符）

        // 根据每部分的内容生成 ASN.1 标记
        val C1xTitle = if (sm2Sequence.C1x.substring(0, 2).toInt(16) > 127) "022100" else "0220"
        val C1yTitle = if (sm2Sequence.C1y.substring(0, 2).toInt(16) > 127) "022100" else "0220"
        val C3Title = "0420"
        val C2Title = "04" + genLenHex(sm2Sequence.C2)

        // 拼接 SEQUENCE 消息
        val sequenceMessage = C1xTitle + sm2Sequence.C1x +
                C1yTitle + sm2Sequence.C1y +
                C3Title + sm2Sequence.C3 +
                C2Title + sm2Sequence.C2

        // 生成 SEQUENCE 的长度
        val sequenceLenHex = genLenHex(sequenceMessage)

        // 返回标准数据
        return "30$sequenceLenHex$sequenceMessage"
    }
}