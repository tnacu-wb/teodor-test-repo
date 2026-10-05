package uk.co.whitbread.integrationtests.framework.wiremock.model

import kotlinx.serialization.Serializable

/**
 * Request matcher submitted to WireMock when removing scoped request-journal events.
 *
 * The URL matcher deliberately covers every path because scenario ownership is carried by
 * [headers], not by any endpoint-specific request attribute.
 */
@Serializable
data class RequestJournalCriteria(
    val urlPattern: String,
    val headers: Map<String, StringValuePattern>,
)
