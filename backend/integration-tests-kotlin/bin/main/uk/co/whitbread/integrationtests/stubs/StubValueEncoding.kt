package uk.co.whitbread.integrationtests.stubs

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.model.BodyPattern

/**
 * Builds a JSON object from Kotlin primitives, nested maps, iterables, and existing JSON elements.
 *
 * Unsupported value types fail immediately so stub authors cannot accidentally fall back to an
 * unsafe or surprising `toString()` representation.
 *
 * @param fields ordered response fields and their structured values.
 * @return a JSON object preserving field order and primitive JSON types.
 * @throws IllegalArgumentException when a value has no explicitly supported JSON representation.
 */
internal fun stubJsonObject(vararg fields: Pair<String, Any?>): JsonObject =
    JsonObject(fields.associate { (name, value) -> name to stubJsonElement(value) })

/**
 * Encodes a value as a quoted JSON string literal suitable for a JSONPath filter expression.
 *
 * @param value untrusted literal value from scenario data.
 * @return a double-quoted literal with JSON escapes for quotes, slashes, controls, and Unicode.
 */
internal fun jsonPathStringLiteral(value: String): String = JsonPrimitive(value).toString()

/** Short-form alias for [jsonPathStringLiteral], for use inside JSONPath filter expressions. */
internal fun literal(value: String): String = jsonPathStringLiteral(value)

/** A body pattern requiring the given JSONPath to match nothing — "this member is absent". */
internal fun absentJsonPath(path: String): BodyPattern = BodyPattern(not = BodyPattern(matchesJsonPath = path))

/**
 * Escapes a dynamic value for an XML or HTML text node without changing its parsed text value.
 *
 * @param value untrusted text inserted between markup tags.
 * @return text with the five XML predefined characters represented as entities.
 */
internal fun escapeMarkupText(value: String): String =
    buildString(value.length) {
        value.forEach { character ->
            append(
                when (character) {
                    '&' -> "&amp;"
                    '<' -> "&lt;"
                    '>' -> "&gt;"
                    '"' -> "&quot;"
                    '\'' -> "&apos;"
                    else -> character
                },
            )
        }
    }

/** Converts one supported Kotlin value into its type-preserving JSON representation. */
private fun stubJsonElement(value: Any?): JsonElement =
    when (value) {
        null -> JsonNull
        is JsonElement -> value
        is String -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        is Number -> JsonPrimitive(value)
        is Map<*, *> ->
            JsonObject(
                value.entries.associate { (key, nestedValue) ->
                    require(key is String) { "Stub JSON object keys must be strings, but found ${key?.let { it::class.simpleName }}" }
                    key to stubJsonElement(nestedValue)
                },
            )
        is Iterable<*> -> JsonArray(value.map(::stubJsonElement))
        else -> throw IllegalArgumentException("Unsupported stub JSON value type: ${value::class.qualifiedName}")
    }
