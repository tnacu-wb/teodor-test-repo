package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.opera.hotelInventory
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import java.time.LocalDate

const val CUSTOM_HOTEL_INVENTORY_ERROR_STUB_ID = "custom.opera.hotel-inventory-error"

/**
 * Answers one Booking-driven hotel-inventory interval with an Opera rejection.
 *
 * When [failedIntervalStart] is omitted, the default must contain one mapping. For split stays,
 * selecting an interval retains every other successful mapping from the generic capability.
 * Carries its own stub id and keeps the default's request matchers, so it is installed with
 * [OPERA_HOTEL_INVENTORY_STUB_ID][uk.co.whitbread.integrationtests.stubs.opera.OPERA_HOTEL_INVENTORY_STUB_ID] passed in `excluded` plus `installStub`.
 */
fun hotelInventoryFailure(
    booking: Booking,
    failedIntervalStart: LocalDate? = null,
): PlannedStub =
    hotelInventory(booking).let { default ->
        val failedMappings =
            default.mappings.filter { mapping ->
                failedIntervalStart == null ||
                    mapping.request.queryParameters
                        ?.get("dateRangeStart")
                        ?.equalTo == failedIntervalStart.toString()
            }
        require(failedMappings.size == 1) {
            "Expected exactly one hotel-inventory interval for failure, found ${failedMappings.size}"
        }

        default.copy(
            id = CUSTOM_HOTEL_INVENTORY_ERROR_STUB_ID,
            mappings =
                default.mappings.map { mapping ->
                    if (mapping in failedMappings) {
                        mapping.copy(
                            response =
                                jsonResponse(
                                    status = 500,
                                    jsonBody = stubJsonObject("message" to "hotel inventory lookup failed"),
                                ),
                        )
                    } else {
                        mapping
                    }
                },
        )
    }
