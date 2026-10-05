package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.hotelConfigs
import uk.co.whitbread.integrationtests.testkit.model.Hotel

const val OPERA_HOTEL_CONFIG_REJECTED_STUB_ID = "opera.hotel-config.rejected"

/**
 * Builds an Opera rejection for the hotel-config read shared by every consumer that resolves the
 * hotel time zone first. One representative journey covers this failure for all of them.
 */
fun hotelConfigFailure(hotel: Hotel): PlannedStub =
    hotelConfigs(listOf(hotel)).rejectedByOpera(
        id = OPERA_HOTEL_CONFIG_REJECTED_STUB_ID,
        detail = "Hotel configuration could not be fetched.",
    )
