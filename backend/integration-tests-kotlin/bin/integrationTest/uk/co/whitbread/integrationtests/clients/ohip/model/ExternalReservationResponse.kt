package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import uk.co.whitbread.integrationtests.testkit.model.IsoLocalDateSerializer
import java.time.LocalDate

@Serializable
data class ExternalReservationResponse(
    val reservationsDetailsResponse: ExternalReservationsDetails? = null,
    val billing: ExternalReservationBilling? = null,
    val previousTotal: Double? = null,
    val balanceOutstanding: Double? = null,
    val newTotal: Double? = null,
    val totalCost: Double? = null,
    val amountPaid: Double? = null,
    val policyCode: String? = null,
    val currencyCode: String? = null,
)

@Serializable
data class ExternalReservationsDetails(
    val reservations: ExternalReservations? = null,
)

@Serializable
data class ExternalReservations(
    val reservationInfo: List<ExternalReservationInfo>? = null,
    val totalPages: Int = 0,
    val offset: Int = 0,
    val limit: Int = 0,
    val hasMore: Boolean = false,
    val totalResults: Int = 0,
)

@Serializable
data class ExternalReservationInfo(
    val reservationIdList: List<ExternalReservationId>? = null,
    val externalReferences: List<ExternalReservationReference>? = null,
    val roomStay: ExternalReservationRoomStay? = null,
    val reservationGuest: ExternalReservationGuest? = null,
    val hotelId: String? = null,
    val hotelName: String? = null,
    val roomStayReservation: Boolean = false,
    val reservationStatus: String? = null,
)

@Serializable
data class ExternalReservationId(
    val id: String? = null,
    val type: String? = null,
)

@Serializable
data class ExternalReservationReference(
    val id: String? = null,
    val idExtension: Int = 0,
    val idContext: String? = null,
)

@Serializable
data class ExternalReservationRoomStay(
    @Serializable(with = IsoLocalDateSerializer::class)
    val arrivalDate: LocalDate? = null,
    @Serializable(with = IsoLocalDateSerializer::class)
    val departureDate: LocalDate? = null,
    // RoomRatesDto is a shared check-in graph. Keep it lossless without duplicating that unrelated graph.
    val roomRates: List<JsonObject>? = null,
    val adultCount: Int = 0,
    val childCount: Int = 0,
    val roomClass: String? = null,
    val roomType: String? = null,
    val numberOfRooms: Int = 0,
    val ratePlanCode: String? = null,
    val rateAmount: ExternalReservationCurrencyAmount? = null,
    val rateSuppressed: Boolean = false,
    val bookingChannelCode: String? = null,
    val fixedRate: Boolean = false,
    val totalAmount: ExternalReservationCurrencyAmount? = null,
    val marketCode: String? = null,
    val sourceCode: String? = null,
    val roomTypeCharged: String? = null,
    val roomNumberLocked: Boolean = false,
    val pseudoRoom: Boolean = false,
)

@Serializable
data class ExternalReservationCurrencyAmount(
    val amount: Double? = null,
    val currencyCode: String? = null,
)

@Serializable
data class ExternalReservationGuest(
    val givenName: String? = null,
    val surname: String? = null,
    val nameTitle: String? = null,
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
    @Serializable(with = IsoLocalDateSerializer::class)
    val birthDate: LocalDate? = null,
    val language: String? = null,
    val guestRestricted: Boolean = false,
    val id: String? = null,
    val type: String? = null,
)

@Serializable
data class ExternalReservationBilling(
    val address: ExternalReservationAddress? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val telephone: String? = null,
    val landline: String? = null,
    val title: String? = null,
)

@Serializable
data class ExternalReservationAddress(
    val companyName: String? = null,
    val countryCode: String? = null,
    val cityName: String? = null,
    val line1: String? = null,
    val line2: String? = null,
    val line3: String? = null,
    val line4: String? = null,
    val postalCode: String? = null,
)
