package uk.co.whitbread.integrationtests.stubs.aem

@Suppress("UNCHECKED_CAST")
fun deepMerge(
    base: Map<String, Any?>,
    overlay: Map<String, Any?>,
): Map<String, Any?> {
    val result = base.toMutableMap()
    for ((key, overlayValue) in overlay) {
        val baseValue = result[key]
        result[key] =
            if (baseValue is Map<*, *> && overlayValue is Map<*, *>) {
                deepMerge(baseValue as Map<String, Any?>, overlayValue as Map<String, Any?>)
            } else {
                overlayValue
            }
    }
    return result
}

@Suppress("UNCHECKED_CAST")
fun toJsonElement(value: Any?): kotlinx.serialization.json.JsonElement =
    when (value) {
        null -> kotlinx.serialization.json.JsonNull
        is Boolean -> kotlinx.serialization.json.JsonPrimitive(value)
        is Number -> kotlinx.serialization.json.JsonPrimitive(value)
        is String -> kotlinx.serialization.json.JsonPrimitive(value)
        is List<*> -> kotlinx.serialization.json.JsonArray(value.map { toJsonElement(it) })
        is Map<*, *> ->
            kotlinx.serialization.json.JsonObject(
                (value as Map<String, Any?>).mapValues { (_, v) -> toJsonElement(v) },
            )
        else -> kotlinx.serialization.json.JsonPrimitive(value.toString())
    }
