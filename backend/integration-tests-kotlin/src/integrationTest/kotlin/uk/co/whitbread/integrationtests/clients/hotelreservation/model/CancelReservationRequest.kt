package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/**
 * Body of `POST /v1/reservations/cancellations`, `POST /v1/reservations/cancellations/rollback`
 * and `PUT /v1/reservations/cancellations/on-hold`.
 *
 * `hotelId` is mandatory in practice: the service forwards the body's value to
 * `ohip-adapter-service` as the Opera `x-hotelid` and never defaults it from the basket.
 *
 * The optional fields are optional for different reasons on the two endpoints. On the
 * cancellation endpoint `reservationIds` and `paymentOption` are overwritten from the basket
 * before the OHIP call, and `token` is the manage-booking authorization the endpoint validates.
 * On the rollback endpoint nothing is read from a basket: `reservationIds` and `paymentOption` are
 * forwarded verbatim, `paymentOption` is what selects the deposit/folio/card work downstream, and
 * `token` is accepted but never validated.
 * On the on-hold endpoint `reservationIds` and `paymentOption` are overwritten too — the ids from
 * the basket items and the option with `null` — and `token` is not read at all.
 */
@Serializable
data class CancelReservationRequest(
    val basketReference: String,
    val hotelId: String,
    val token: String? = null,
    val reservationIds: List<String>? = null,
    val paymentOption: CancelReservationPaymentOption? = null,
    val reservationOverrideReason: CancelReservationOverrideReason? = null,
)

/** Payment option the caller declares; it selects the pre-cancellation work in ohip-adapter. */
@Serializable
enum class CancelReservationPaymentOption {
    PAY_NOW,
    PAY_ON_ARRIVAL,
    RESERVE_WITHOUT_CARD,
    ACCOUNT_COMPANY,
}

/** Replaces Opera's default `CXL` / `Trip Cancelled` cancellation reason. */
@Serializable
data class CancelReservationOverrideReason(
    val reasonCode: String,
    val reasonName: String,
    val callerName: String,
    val managerName: String? = null,
)
