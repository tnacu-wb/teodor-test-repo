package uk.co.whitbread.integrationtests.stubs

import kotlinx.serialization.json.JsonElement
import uk.co.whitbread.integrationtests.framework.wiremock.model.ResponseDefinition

/**
 * Response envelopes shared by the low-level stub builders.
 *
 * These exist so a builder states only its status and payload, leaving the content type — which
 * was previously repeated in 45 of 46 response definitions — in one place.
 *
 * Every value is assigned explicitly rather than defaulted on [ResponseDefinition]. WireMock
 * mappings are serialized with `encodeDefaults = false`, so a status or content type expressed
 * as a constructor default would be omitted from the JSON sent to WireMock instead of inherited.
 */
private const val JSON_CONTENT_TYPE = "application/json"
private const val XML_CONTENT_TYPE = "text/xml;charset=UTF-8"

/** JSON response carrying an already-rendered [body]. */
internal fun jsonResponse(
    body: String,
    status: Int = 200,
): ResponseDefinition =
    ResponseDefinition(
        status = status,
        headers = mapOf("Content-Type" to JSON_CONTENT_TYPE),
        body = body,
    )

/** JSON response carrying a structured [jsonBody]. */
internal fun jsonResponse(
    jsonBody: JsonElement,
    status: Int = 200,
): ResponseDefinition =
    ResponseDefinition(
        status = status,
        headers = mapOf("Content-Type" to JSON_CONTENT_TYPE),
        jsonBody = jsonBody,
    )

/** XML response carrying an already-rendered [body]. */
internal fun xmlResponse(
    body: String,
    status: Int = 200,
): ResponseDefinition =
    ResponseDefinition(
        status = status,
        headers = mapOf("Content-Type" to XML_CONTENT_TYPE),
        body = body,
    )
