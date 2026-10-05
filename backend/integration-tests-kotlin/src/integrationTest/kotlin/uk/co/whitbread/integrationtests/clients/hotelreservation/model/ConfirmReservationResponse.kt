package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.co.whitbread.integrationtests.testkit.model.IsoLocalDateSerializer
import java.time.LocalDate

@Serializable
data class ConfirmReservationResponse(
    val reservationIdList: List<ConfirmReservationUniqueId>? = null,
    val roomStay: ConfirmReservationRoomStay? = null,
    val reservationGuest: ConfirmReservationCustomer? = null,
    val hotelId: String? = null,
    val reservationStatus: String? = null,
    @SerialName("partialPaid")
    val isPartialPaid: Boolean,
)

@Serializable
data class ConfirmReservationUniqueId(
    val id: String? = null,
    val type: String? = null,
)

@Serializable
data class ConfirmReservationRoomStay(
    @Serializable(with = IsoLocalDateSerializer::class)
    val arrivalDate: LocalDate? = null,
    @Serializable(with = IsoLocalDateSerializer::class)
    val departureDate: LocalDate? = null,
)

@Serializable
data class ConfirmReservationCustomer(
    val givenName: String? = null,
    val surName: String? = null,
)
