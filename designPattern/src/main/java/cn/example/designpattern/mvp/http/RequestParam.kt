package cn.example.designpattern.mvp.http

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

class RequestParam {
    private val gson = Gson()
    val jsonObject: JsonObject = JsonObject()

    init {
        initCommonParams()
    }

    // 初始化公共字段
    private fun initCommonParams() {
        //jsonObject.addProperty("client_id", "your_client_id")
        //jsonObject.addProperty("app_version", "1.0.0")
    }

    // 添加基本键值对
    fun put(key: String, value: String) {
        jsonObject.addProperty(key, value)
    }

    fun put(map: Map<String, String>) {
        for ((key, value) in map) {
            put(key, value)
        }
    }

    fun put(key: String, value: Number) {
        jsonObject.addProperty(key, value)
    }

    fun put(key: String, value: JsonArray) {
        jsonObject.add(key, value)
    }

    fun put(key: String, value: JsonObject) {
        jsonObject.add(key, value)
    }

    fun put(key: String, value: JsonElement) {
        jsonObject.add(key, value)
    }

    // 添加或更新 `data` 字段中的键值对
    fun addInputData(inputDataKey: String, inputDataValue: String) {
        val dataObject = JsonObject()

        // 检查是否已有 `data` 字段
        if (jsonObject.has("data")) {
            val dataElement = jsonObject["data"]
            when {
                dataElement.isJsonObject -> {
                    dataElement.asJsonObject.entrySet().forEach { entry ->
                        dataObject.add(entry.key, entry.value)
                    }
                }
                dataElement.isJsonPrimitive -> {
                    val existingData = gson.fromJson(dataElement.asString, JsonObject::class.java)
                    existingData.entrySet().forEach { entry ->
                        dataObject.add(entry.key, entry.value)
                    }
                }
            }
        }

        // 添加新的键值对
        dataObject.addProperty(inputDataKey, inputDataValue)

        // 更新 `data` 字段
        put("data", dataObject.toString())
    }

    // 插入 `data` 字段，通过 Map 添加多个键值对
    fun addInputData(map: Map<String, String>) {
        for ((key, value) in map) {
            addInputData(key, value)
        }
    }

    // 添加非空字段到 `RequestParam`
    fun addIfNotEmpty(key: String, value: String?) {
        value?.takeIf { it.isNotBlank() }?.let {
            put(key, it)
        }
    }

    // 重载 `toString` 返回 JSON 字符串
    override fun toString(): String {
        return jsonObject.toString()
    }
}
