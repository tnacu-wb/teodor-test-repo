package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * The single-reservation detail response from `GET /ohip/v1/reservation/reservationId`.
 *
 * Models only the fields journeys assert; the decoder ignores the rest of the payload. The money
 * fields are `BigDecimal` server-side and are kept as `Double` here so scenarios compare values
 * rather than wire scales.
 */
@Serializable
data class ReservationIdDetailsResponse(
    val reservationsDetailsResponse: ReservationsIdDetails? = null,
    val billing: ReservationBilling? = null,
    val previousTotal: Double? = null,
    val balanceOutstanding: Double? = null,
    val newTotal: Double? = null,
    val totalCost: Double? = null,
    val amountPaid: Double? = null,
    val policyCode: String? = null,
    val currencyCode: String? = null,
)

@Serializable
data class ReservationsIdDetails(
    val reservations: ReservationsIdList? = null,
)

@Serializable
data class ReservationsIdList(
    val reservation: List<ReservationIdDetailsEntry> = emptyList(),
)

@Serializable
data class ReservationIdDetailsEntry(
    val reservationIdList: List<ReservationUniqueId> = emptyList(),
    val hotelId: String? = null,
    val reservationStatus: String? = null,
)

@Serializable
data class ReservationUniqueId(
    val id: String? = null,
    val type: String? = null,
)

@Serializable
data class ReservationBilling(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
)
