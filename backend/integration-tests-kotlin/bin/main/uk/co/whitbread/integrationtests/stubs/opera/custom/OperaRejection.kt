package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject

/**
 * Re-ids a default stub and swaps every response for an Opera 400 rejection envelope.
 *
 * The default matchers are retained, so the failure hits exactly the requests the adapter would
 * otherwise succeed with. Install with the matching default excluded.
 */
internal fun PlannedStub.rejectedByOpera(
    id: String,
    detail: String,
    status: Int = 400,
): PlannedStub =
    copy(
        id = id,
        mappings = mappings.map { mapping -> mapping.rejectedByOpera(detail, status) },
    )

/** Swaps one mapping's response for an Opera rejection envelope, keeping its matcher. */
internal fun StubMapping.rejectedByOpera(
    detail: String,
    status: Int = 400,
): StubMapping =
    copy(
        response =
            jsonResponse(
                status = status,
                jsonBody =
                    stubJsonObject(
                        "type" to if (status >= 500) "Internal Server Error" else "Bad Request",
                        "title" to detail,
                        "detail" to detail,
                        "o:errorCode" to "OPERAWS-GEN01278",
                        "language" to "en",
                    ),
            ),
    )
