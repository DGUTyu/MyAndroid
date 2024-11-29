package cn.example.designpattern.mvp.model

data class PostByIdResponseBean(
        val gson: GsonBean,
        val jsonObject: JsonObjectBean,
        val id: Int
)

data class GsonBean(
        val builderFactories: List<Any?> = emptyList(),
        val builderHierarchyFactories: List<Any?> = emptyList(),
        val calls: Map<String, Any?> = emptyMap(),
        val complexMapKeySerialization: Boolean = false,
        val constructorConstructor: ConstructorConstructorBean,
        val dateStyle: Int = 0,
        val excluder: ExcluderBean,
        val factories: List<Any?> = emptyList(),
        val fieldNamingStrategy: String = "IDENTITY",
        val generateNonExecutableJson: Boolean = false,
        val htmlSafe: Boolean = true,
        val instanceCreators: Map<String, Any?> = emptyMap(),
        val jsonAdapterFactory: JsonAdapterFactoryBean,
        val lenient: Boolean = false,
        val longSerializationPolicy: String = "DEFAULT",
        val prettyPrinting: Boolean = false,
        val serializeNulls: Boolean = false,
        val serializeSpecialFloatingPointValues: Boolean = false,
        val timeStyle: Int = 0,
        val typeTokenCache: Map<String, Any?> = emptyMap()
)

data class ConstructorConstructorBean(
        val instanceCreators: Map<String, Any?> = emptyMap()
)

data class ExcluderBean(
        val deserializationStrategies: List<Any?> = emptyList(),
        val modifiers: Int = 0,
        val requireExpose: Boolean = false,
        val serializationStrategies: List<Any?> = emptyList(),
        val serializeInnerClasses: Boolean = true,
        val version: Int = -1
)

data class JsonAdapterFactoryBean(
        val constructorConstructor: ConstructorConstructorBean
)

data class JsonObjectBean(
        val title: String,
        val body: String,
        val userId: Int
)