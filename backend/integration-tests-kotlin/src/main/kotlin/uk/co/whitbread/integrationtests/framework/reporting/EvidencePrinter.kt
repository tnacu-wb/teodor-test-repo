package uk.co.whitbread.integrationtests.framework.reporting

import uk.co.whitbread.integrationtests.framework.http.HttpEvidence

/**
 * Converts structured HTTP evidence into ordered human-readable artifact sections.
 *
 * @param prefix optional test-author label appended to request and response headings.
 * @param evidence captured exchange to render.
 * @param failure optional decoding or capture failure rendered after the exchange.
 * @return request, response, and optional failure sections in diagnostic order.
 */
internal fun evidenceSections(
    prefix: String?,
    evidence: HttpEvidence,
    failure: Throwable? = null,
): List<EvidenceSection> =
    buildList {
        add(
            EvidenceSection(
                sectionName(prefix, "HTTP Request"),
                evidence.request?.render() ?: "<no request evidence captured>",
            ),
        )
        add(
            EvidenceSection(
                sectionName(prefix, "HTTP Response"),
                evidence.response?.render() ?: "<no response evidence captured>",
            ),
        )
        failure?.let {
            add(
                EvidenceSection(
                    sectionName(prefix, "HTTP Failure"),
                    "${it::class.qualifiedName}: ${it.message ?: "<no message>"}",
                ),
            )
        }
    }

/** Renders one structured request into the legacy-readable evidence layout. */
private fun uk.co.whitbread.integrationtests.framework.http.HttpRequestEvidence.render(): String =
    buildString {
        appendLine("$method $url")
        if (headers.isNotEmpty()) {
            appendLine()
            headers.forEach { (name, value) -> appendLine("$name: $value") }
        }
        body?.let {
            appendLine()
            appendLine(it.render())
        }
    }.trimEnd()

/** Renders one structured response into the legacy-readable evidence layout. */
private fun uk.co.whitbread.integrationtests.framework.http.HttpResponseEvidence.render(): String =
    buildString {
        appendLine("HTTP $status")
        if (headers.isNotEmpty()) {
            appendLine()
            headers.forEach { (name, value) -> appendLine("$name: $value") }
        }
        appendLine()
        appendLine(body.render())
    }.trimEnd()

/** Adds [prefix] to [name] when the author supplied a non-blank label. */
private fun sectionName(
    prefix: String?,
    name: String,
): String = if (prefix.isNullOrBlank()) name else "$name: $prefix"
