package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Request body for `PUT /ohip/v1/reservations/link-leisure-customer`
 * (`LinkReservationToLeisureCustomerRequestDto`). The ids are sent as a raw JSON array so a
 * repeated id reaches the service, whose `Set<String>` binding collapses it before the fan-out.
 * [customerAccountId] is the CDH leisure customer account written to character UDF `UDFC35`;
 * the `PI` booking-channel marker in `UDFC09` is hard-coded by the service and has no request
 * field.
 */
@Serializable
data class LinkReservationToLeisureCustomerRequest(
    val hotelId: String,
    val reservationIds: List<String>,
    val customerAccountId: String,
)
