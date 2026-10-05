package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** Request body for `PUT /ohip/v1/reservations/confirmAmend`. */
@Serializable
data class ConfirmAmendRequest(
    val hotelId: String,
    val bookingChannel: UpdateBookingChannel,
    val tempReservations: List<String>,
    val originalReservations: List<String>,
    val linkAmendReservations: Map<String, String> = emptyMap(),
    val sendEmailConfirmation: Boolean? = null,
    val sendEmailInvoice: Boolean? = null,
    val markAsPayOnArrival: Boolean? = null,
    val clearCcAgentIdUdf: Boolean? = null,
)
