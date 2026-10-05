package uk.co.whitbread.integrationtests.stubs.datatrans.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.datatrans.secureFieldsInit
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking

const val DATATRANS_SECURE_FIELDS_INIT_FAILURE_STUB_ID = "custom.datatrans.secure-fields-init-failure"

/**
 * Builds a Datatrans rejection for an otherwise valid Secure Fields initialization request.
 *
 * The request comes from the successful mapping. This keeps its strict amount and currency
 * matchers while swapping only the gateway response. Carries its own stub id, so it is installed
 * with [DATATRANS_SECURE_FIELDS_INIT_STUB_ID][uk.co.whitbread.integrationtests.stubs.datatrans.DATATRANS_SECURE_FIELDS_INIT_STUB_ID] passed in `excluded` plus `installStub`.
 */
fun secureFieldsInitFailure(booking: Booking): PlannedStub {
    val default = secureFieldsInit(booking)

    return default.copy(
        id = DATATRANS_SECURE_FIELDS_INIT_FAILURE_STUB_ID,
        mappings =
            listOf(
                default.mappings.single().copy(
                    response =
                        jsonResponse(
                            status = 500,
                            jsonBody =
                                stubJsonObject(
                                    "error" to
                                        mapOf(
                                            "code" to "INTERNAL_ERROR",
                                            "message" to "Datatrans could not initialize Secure Fields",
                                        ),
                                ),
                        ),
                ),
            ),
    )
}
