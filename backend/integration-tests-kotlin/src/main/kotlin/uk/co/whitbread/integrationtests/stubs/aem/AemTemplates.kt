package uk.co.whitbread.integrationtests.stubs.aem

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull

object AemTemplates {
    @Suppress("UNCHECKED_CAST")
    private val hotelDetailBase: Map<String, Any?> by lazy {
        val text =
            this::class.java.classLoader
                .getResource("wiremock/aem/aem-hotel-detail-base.json")!!
                .readText()
        val element = Json.parseToJsonElement(text)
        jsonElementToMap(element) as Map<String, Any?>
    }

    fun hotelDetailBase(): Map<String, Any?> = hotelDetailBase
}

private fun jsonElementToMap(element: JsonElement): Any? =
    when (element) {
        is JsonNull -> null
        is JsonPrimitive ->
            when {
                element.isString -> element.content
                element.booleanOrNull != null -> element.booleanOrNull
                element.longOrNull != null -> element.longOrNull
                element.doubleOrNull != null -> element.doubleOrNull
                else -> element.content
            }
        is JsonArray -> element.map { jsonElementToMap(it) }
        is JsonObject -> element.mapValues { (_, v) -> jsonElementToMap(v) }
    }
