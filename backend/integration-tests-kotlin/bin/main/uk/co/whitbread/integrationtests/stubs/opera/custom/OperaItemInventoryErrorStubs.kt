package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.opera.hotelItemInventory
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking

const val CUSTOM_ITEM_INVENTORY_ERROR_STUB_ID = "custom.opera.item-inventory-error"

/**
 * Answers one interval of the generic item-inventory capability with an Opera rejection.
 *
 * Every mapping and request matcher is retained so a selected interval can fail while the other
 * intervals continue to represent the Booking-driven successful state. Carries its own stub id,
 * so it is installed with [OPERA_ITEM_INVENTORY_STUB_ID][uk.co.whitbread.integrationtests.stubs.opera.OPERA_ITEM_INVENTORY_STUB_ID] passed in `excluded` plus `installStub`.
 */
fun hotelItemInventoryFailure(
    booking: Booking,
    failedIntervalIndex: Int = 0,
): PlannedStub =
    hotelItemInventory(booking).let { default ->
        require(failedIntervalIndex in default.mappings.indices) {
            "Item-inventory interval index $failedIntervalIndex is outside ${default.mappings.indices}"
        }
        default.copy(
            id = CUSTOM_ITEM_INVENTORY_ERROR_STUB_ID,
            mappings =
                default.mappings.mapIndexed { index, mapping ->
                    if (index != failedIntervalIndex) {
                        mapping
                    } else {
                        mapping.copy(
                            response =
                                jsonResponse(
                                    status = 500,
                                    jsonBody = stubJsonObject("message" to "hotel item inventory lookup failed"),
                                ),
                        )
                    }
                },
        )
    }
