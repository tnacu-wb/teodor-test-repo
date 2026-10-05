package uk.co.whitbread.integrationtests.framework.http

import kotlinx.serialization.json.Json

/**
 * Single serializer configuration for service-under-test calls.
 *
 * The suite's shared HTTP client encodes request bodies through ContentNegotiation with this
 * instance, and [ServiceApiClient] decodes responses with it, so both directions of one
 * exchange always agree. Serializer changes — a module, coercion, leniency — belong here and
 * nowhere else.
 */
val ServiceJson =
    Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
