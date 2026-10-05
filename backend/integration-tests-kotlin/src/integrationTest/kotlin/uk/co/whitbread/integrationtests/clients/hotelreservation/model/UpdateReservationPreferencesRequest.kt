package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/** Request body for `PUT /v1/reservations/preferences`. */
@Serializable
data class UpdateReservationPreferencesRequest(
    val hotelId: String,
    val reservationsIds: List<String>,
    val preferencesCollections: List<ReservationPreferenceCollection>,
)

/** One preference category and its Opera preference values. */
@Serializable
data class ReservationPreferenceCollection(
    val preferenceType: String,
    val preferences: List<String>,
)
