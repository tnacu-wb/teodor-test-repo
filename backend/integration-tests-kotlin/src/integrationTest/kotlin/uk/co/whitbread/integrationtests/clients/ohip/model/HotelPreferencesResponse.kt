package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class HotelPreferencesResponse(
    val hotelPreferences: List<HotelPreferenceRow>? = null,
)

@Serializable
data class HotelPreferenceRow(
    val description: String? = null,
    val code: String? = null,
    val preferenceGroup: String? = null,
    val housekeeping: Boolean = false,
    val orderSequence: Int? = null,
    val hotelId: String? = null,
)
