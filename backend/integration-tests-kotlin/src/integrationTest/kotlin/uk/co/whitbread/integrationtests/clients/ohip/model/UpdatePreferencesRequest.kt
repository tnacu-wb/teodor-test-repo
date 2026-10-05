package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Request body for `PUT /ohip/v1/reservations/preferences`
 * (`ReservationPreferencesRequestDto`). [reservationsIds] is sent as a raw JSON array and binds
 * to a `List`, so every entry — duplicates included — drives its own Opera reservation update.
 * The same [preferencesCollections] content is written to every one of them.
 *
 * Each body is mapped by filtering the full [reservationsIds] list for the entry's id, so a
 * duplicated id yields one PUT per entry whose body repeats that id in `reservationIdList`. The
 * semantically pinned reservation-update stub requires exactly one identity entry, so it supports
 * distinct-id worlds only and duplicate-id requests are out of its scope.
 */
@Serializable
data class UpdatePreferencesRequest(
    val hotelId: String,
    val reservationsIds: List<String>,
    val preferencesCollections: List<PreferencesCollection>,
)

/**
 * One requested preference category (`PreferencesCollectionDto`): the Opera preference type and
 * the values held under it, each of which the service emits as its own Opera
 * `preference[].preferenceValue`.
 */
@Serializable
data class PreferencesCollection(
    val preferenceType: String,
    val preferences: List<String>,
)
