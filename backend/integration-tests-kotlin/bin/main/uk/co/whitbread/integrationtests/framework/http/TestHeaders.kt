package uk.co.whitbread.integrationtests.framework.http

import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern

/**
 * W3C baggage header carried by every service-under-test call.
 *
 * Its first member is the current `testId`, which is what correlates a call with the scenario's
 * WireMock stubs: data-bearing stubs match that member so concurrent tests do not consume each
 * other's mappings. Scenarios can add further members — feature-flag overrides are the current
 * example — so the header is not test-ID-only, and typed clients build its value through
 * `scenarioBaggage` rather than assembling members themselves.
 *
 * This file owns the header's grammar: its name, the test-ID member, and the matcher that
 * finds that member inside a complete header value. Callers that contribute further
 * members compose them through [baggageHeaderValue] so the separator stays defined here.
 */
const val BAGGAGE_HEADER = "baggage"
const val TEST_ID_BAGGAGE_KEY = "wb-test-id"

/** Joins baggage members into one header value using the W3C list separator. */
fun baggageHeaderValue(members: List<String>): String = members.joinToString(",")

fun testIdHeaderValue(testId: String): String = "$TEST_ID_BAGGAGE_KEY=$testId"

fun testIdHeaderMatcher(testId: String): StringValuePattern =
    StringValuePattern(
        matches = "(^|.*,\\s*)${Regex.escape(testIdHeaderValue(testId))}(\\s*;[^,]*)?\\s*(,.*|$)",
    )

fun StringValuePattern.matchesTestId(testId: String): Boolean =
    equalTo == testIdHeaderValue(testId) || matches == testIdHeaderMatcher(testId).matches
