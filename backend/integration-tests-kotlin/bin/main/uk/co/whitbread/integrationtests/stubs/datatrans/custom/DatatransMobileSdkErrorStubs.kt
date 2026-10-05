package uk.co.whitbread.integrationtests.stubs.datatrans.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.datatrans.mobileSdkInit
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking

const val DATATRANS_MOBILE_SDK_INIT_FAILURE_STUB_ID = "custom.datatrans.mobile-sdk-init-failure"

/**
 * Builds a Datatrans rejection for an otherwise valid Mobile SDK initialisation request.
 *
 * The request comes from the successful mapping. This keeps its strict amount and currency
 * matchers while swapping only the gateway response. Carries its own stub id, so it is installed
 * with [DATATRANS_MOBILE_SDK_INIT_STUB_ID][uk.co.whitbread.integrationtests.stubs.datatrans.DATATRANS_MOBILE_SDK_INIT_STUB_ID] passed in `excluded` plus `installStub`.
 */
fun mobileSdkInitFailure(booking: Booking): PlannedStub {
    val default = mobileSdkInit(booking)

    return default.copy(
        id = DATATRANS_MOBILE_SDK_INIT_FAILURE_STUB_ID,
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
                                            "message" to "Datatrans could not initialize the Mobile SDK transaction",
                                        ),
                                ),
                        ),
                ),
            ),
    )
}
