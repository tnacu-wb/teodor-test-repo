package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/**
 * Response of `POST /v1/reservations/guests`, modeling only the fields journeys assert.
 *
 * The service echoes the basket reference and nothing else; the client decodes with
 * `ignoreUnknownKeys`, so later scenarios add fields here as they start asserting them.
 */
@Serializable
data class ReservationGuestResponse(
    val basketReference: String,
)
