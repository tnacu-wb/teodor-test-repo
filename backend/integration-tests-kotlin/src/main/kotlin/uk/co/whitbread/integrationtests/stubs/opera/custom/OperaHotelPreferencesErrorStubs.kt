package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.hotelPreferences
import uk.co.whitbread.integrationtests.testkit.model.Hotel

const val OPERA_HOTEL_PREFERENCES_REJECTED_STUB_ID = "opera.hotel-preferences.rejected"

/** Builds an Opera rejection for the hotel-preferences read, installed with the default excluded. */
fun hotelPreferencesFailure(hotel: Hotel): PlannedStub =
    hotelPreferences(listOf(hotel)).rejectedByOpera(
        id = OPERA_HOTEL_PREFERENCES_REJECTED_STUB_ID,
        detail = "Hotel preferences could not be fetched.",
    )
