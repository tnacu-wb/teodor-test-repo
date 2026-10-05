package uk.co.whitbread.integrationtests.framework.wiremock

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestJournalCriteria
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping

/**
 * Minimal WireMock Admin API client used by the integration framework.
 *
 * This adapter only exposes operations the suite needs for dynamic stub installation
 * and scoped cleanup.
 *
 * @property baseUrl externally reachable base URL of this WireMock Admin API.
 * @param client shared HTTP transport used for Admin API calls. The caller retains ownership.
 */
class WireMockAdmin(
    val baseUrl: String,
    private val client: HttpClient,
) {
    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = false
        }

    /**
     * Registers [mapping].
     *
     * The stub ID WireMock assigns is deliberately not returned. Scoped cleanup resolves IDs from
     * [listStubs], so an install-time ID has no consumer, and requiring one would abort a
     * successful install over a value nobody reads.
     *
     * @throws IllegalStateException if WireMock returns a non-2xx response, or a successful
     * response whose body is not a JSON object.
     */
    suspend fun stub(mapping: StubMapping) {
        val operation = "POST /__admin/mappings"
        val response =
            client.post("$baseUrl/__admin/mappings") {
                contentType(ContentType.Application.Json)
                setBody(mapping)
            }
        response.ensureSuccess(operation)

        // The body is read only to reject a 200 that is not the Admin contract. Believing one
        // would record an installed mapping that WireMock never registered, so the scenario would
        // fail several layers later against an evidence artifact claiming the stub was installed.
        response.objectBody(operation)
    }

    /**
     * Removes a single stub mapping by WireMock stub [id].
     *
     * HTTP 404 is treated as success because the desired final state has already been
     * reached, making repeated or concurrent cleanup idempotent.
     */
    suspend fun removeStub(id: String) {
        val response = client.delete("$baseUrl/__admin/mappings/$id")
        // Consume the body even on paths that don't need it: Ktor only returns a connection to
        // the keep-alive pool once the body is read, and an unconsumed response closes the
        // connection instead. At cleanup volume that exhausts the OS ephemeral-port range.
        val body = response.bodyAsText()
        if (response.status == HttpStatusCode.NotFound) return
        if (!response.status.isSuccess()) {
            error(
                "WireMock Admin DELETE /__admin/mappings/$id at $baseUrl failed: ${response.status}" +
                    if (body.isNotBlank()) " — $body" else "",
            )
        }
    }

    /**
     * Removes request-journal events matching [criteria].
     *
     * WireMock returns the removed request details. Only their count is retained so cleanup
     * evidence does not keep another copy of potentially large recorded responses.
     *
     * @return number of request events removed from this WireMock journal.
     * @throws IllegalStateException if WireMock returns a non-2xx response, or a successful
     * response whose body is not an object carrying an event array.
     */
    suspend fun removeRequests(criteria: RequestJournalCriteria): Int {
        val operation = "POST /__admin/requests/remove"
        val response =
            client.post("$baseUrl/__admin/requests/remove") {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(RequestJournalCriteria.serializer(), criteria))
            }
        response.ensureSuccess(operation)
        val body = response.objectBody(operation)
        val events =
            (body["serveEvents"] ?: body["requests"]) as? JsonArray
                ?: protocolError(operation, "response is missing a 'serveEvents' array")

        return events.size
    }

    /**
     * Counts recorded request-journal events matching [pattern].
     *
     * This reports what the system under test actually did, not what was installed. Scenario
     * ownership is expressed by [pattern] itself, normally through a baggage header matcher.
     *
     * @throws IllegalStateException if WireMock returns a non-2xx response, a body without a
     * numeric `count`, or a body reporting that the request journal is disabled.
     */
    suspend fun countRequests(pattern: RequestPattern): Int {
        val operation = "POST /__admin/requests/count"
        val response =
            client.post("$baseUrl/__admin/requests/count") {
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(RequestPattern.serializer(), pattern))
            }
        response.ensureSuccess(operation)
        val body = response.objectBody(operation)

        // A disabled journal answers 200 with `count: 0` instead of failing. Reading that as a
        // genuine zero would let every "this upstream was never called" assertion pass while
        // proving nothing, so the disabled journal is rejected rather than counted.
        if ((body["requestJournalDisabled"] as? JsonPrimitive)?.booleanOrNull == true) {
            protocolError(operation, "the request journal is disabled, so requests cannot be counted")
        }

        return (body["count"] as? JsonPrimitive)?.intOrNull
            ?: protocolError(operation, "response is missing a numeric 'count'")
    }

    /**
     * Lists registered stub mappings for diagnostics and scoped cleanup.
     *
     * A successful response must carry a `mappings` array. An absent array is rejected
     * rather than read as an empty list: callers such as scoped cleanup cannot distinguish
     * "this WireMock holds no stubs" from "this response did not describe the stubs", and
     * treating the second as the first lets cleanup report success while mappings remain.
     *
     * @throws IllegalStateException if WireMock returns a non-2xx response, or a successful
     * response whose body is not an object carrying a `mappings` array.
     */
    suspend fun listStubs(): List<StubMapping> {
        val operation = "GET /__admin/mappings"
        val response = client.get("$baseUrl/__admin/mappings")
        response.ensureSuccess(operation)
        val body = response.objectBody(operation)
        val mappings =
            body["mappings"] as? JsonArray
                ?: protocolError(operation, "response is missing a 'mappings' array")

        return json.decodeFromJsonElement(ListSerializer(StubMapping.serializer()), mappings)
    }

    private suspend fun HttpResponse.ensureSuccess(operation: String) {
        if (status.isSuccess()) return
        // Not runCatching: it catches Throwable, and bodyAsText is a suspension point. Absorbing
        // CancellationException here would report a cancelled call as an ordinary failure and
        // bypass MockInstaller's cancellation handling.
        val errBody =
            try {
                bodyAsText()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                ""
            }
        error(
            "WireMock Admin $operation at $baseUrl failed: $status" +
                if (errBody.isNotBlank()) " — $errBody" else "",
        )
    }

    /**
     * Decodes a successful response body as a JSON object.
     *
     * Parsing is guarded so a non-JSON success, such as a proxy or restarting WireMock
     * answering 200 with HTML or an empty body, still fails with the operation and target URL
     * rather than an opaque parser message.
     */
    private suspend fun HttpResponse.objectBody(operation: String): JsonObject {
        val text = bodyAsText()
        val element =
            try {
                json.parseToJsonElement(text)
            } catch (_: SerializationException) {
                protocolError(operation, "response body is not valid JSON")
            }

        return element as? JsonObject
            ?: protocolError(operation, "response body is not a JSON object")
    }

    /** Reports a successful response whose body did not honour the Admin API contract. */
    private fun protocolError(
        operation: String,
        reason: String,
    ): Nothing = error("WireMock Admin $operation at $baseUrl returned an unexpected body: $reason")

    override fun toString(): String = baseUrl
}
