package uk.co.whitbread.integrationtests.framework.http

import io.ktor.client.plugins.api.SendingRequest
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.util.AttributeKey

/** Maximum UTF-8 byte count retained for one captured HTTP request or response body. */
internal const val MAX_EVIDENCE_BODY_BYTES: Int = 64 * 1024

/** Request attribute key carrying the last structured request captured for one call. */
private val LastRequestKey = AttributeKey<HttpRequestEvidence>("HttpEvidenceLastRequest")

/** Request attribute key carrying the last structured response captured for one call. */
private val LastResponseKey = AttributeKey<HttpResponseEvidence>("HttpEvidenceLastResponse")

/**
 * Bounded HTTP body retained for scenario evidence.
 *
 * @property text retained body text without the truncation marker.
 * @property originalByteCount UTF-8 size of the complete body.
 * @property retainedByteCount UTF-8 size of [text].
 * @property truncated whether bytes were removed to satisfy the evidence limit.
 */
data class EvidenceBody(
    val text: String,
    val originalByteCount: Int,
    val retainedByteCount: Int,
    val truncated: Boolean,
) {
    /** Renders the retained text and an explicit byte-count marker when truncated. */
    fun render(): String =
        if (truncated) {
            "$text\n[truncated: originalBytes=$originalByteCount, retainedBytes=$retainedByteCount]"
        } else {
            text
        }
}

/**
 * Structured request evidence captured before a service-under-test call is sent.
 *
 * @property method HTTP method.
 * @property url complete request URL including query parameters.
 * @property headers complete synthetic request headers without redaction.
 * @property body optional bounded request body.
 */
data class HttpRequestEvidence(
    val method: String,
    val url: String,
    val headers: Map<String, String>,
    val body: EvidenceBody?,
)

/**
 * Structured response evidence captured before typed success-body decoding.
 *
 * @property status numeric HTTP response status.
 * @property headers complete response headers without redaction.
 * @property body bounded raw response body.
 */
data class HttpResponseEvidence(
    val status: Int,
    val headers: Map<String, String>,
    val body: EvidenceBody,
)

/**
 * Captured structured HTTP request and response for a service-under-test call.
 *
 * Values are nullable because a failure can happen before either half of the exchange is
 * captured.
 *
 * @property request bounded request evidence when request sending began.
 * @property response bounded response evidence when a response arrived.
 */
data class HttpEvidence(
    val request: HttpRequestEvidence?,
    val response: HttpResponseEvidence?,
)

/**
 * Ktor client plugin that records structured, bounded request and response evidence.
 *
 * The plugin stores evidence on request attributes rather than printing. Tests explicitly attach
 * normal exchanges to their scenario sink, while decoding failures attach automatically. All data
 * is synthetic, so headers and fields are preserved without redaction.
 */
val HttpEvidencePlugin =
    createClientPlugin("HttpEvidence") {

        on(SendingRequest) { request, content ->
            val url = request.url.buildString()
            val method = request.method.value

            val headers =
                request.headers
                    .entries()
                    .associate { (key, values) -> key to values.joinToString(", ") }

            val body =
                when (content) {
                    is TextContent -> content.text
                    is OutgoingContent.ByteArrayContent -> content.bytes().decodeToString()
                    else -> null
                }

            request.attributes.put(
                LastRequestKey,
                HttpRequestEvidence(
                    method = method,
                    url = url,
                    headers = headers,
                    body = body?.let(::boundedEvidenceBody),
                ),
            )
        }

        onResponse { response ->
            val headers =
                response.headers
                    .entries()
                    .associate { (key, values) -> key to values.joinToString(", ") }

            val body = response.bodyAsText()

            response.request.attributes.put(
                LastResponseKey,
                HttpResponseEvidence(
                    status = response.status.value,
                    headers = headers,
                    body = boundedEvidenceBody(body),
                ),
            )
        }
    }

/**
 * Returns the bounded request/response evidence captured for this [HttpResponse].
 *
 * @return structured exchange with nullable halves when capture did not reach that stage.
 */
fun HttpResponse.httpEvidence(): HttpEvidence =
    HttpEvidence(
        request = request.attributes.getOrNull(LastRequestKey),
        response = request.attributes.getOrNull(LastResponseKey),
    )

/**
 * Bounds [text] to [maxBytes] UTF-8 bytes without splitting a Unicode code point.
 *
 * @param text complete body text.
 * @param maxBytes maximum retained UTF-8 byte count; must be non-negative.
 * @return bounded body plus original and retained byte counts.
 */
internal fun boundedEvidenceBody(
    text: String,
    maxBytes: Int = MAX_EVIDENCE_BODY_BYTES,
): EvidenceBody {
    require(maxBytes >= 0) { "maxBytes must not be negative" }
    val originalByteCount = text.encodeToByteArray().size
    if (originalByteCount <= maxBytes) {
        return EvidenceBody(text, originalByteCount, originalByteCount, truncated = false)
    }

    var charIndex = 0
    var retainedByteCount = 0
    while (charIndex < text.length) {
        val codePoint = Character.codePointAt(text, charIndex)
        val charCount = Character.charCount(codePoint)
        val nextIndex = charIndex + charCount
        val codePointBytes = text.substring(charIndex, nextIndex).encodeToByteArray().size
        if (retainedByteCount + codePointBytes > maxBytes) break
        retainedByteCount += codePointBytes
        charIndex = nextIndex
    }

    return EvidenceBody(
        text = text.substring(0, charIndex),
        originalByteCount = originalByteCount,
        retainedByteCount = retainedByteCount,
        truncated = true,
    )
}
