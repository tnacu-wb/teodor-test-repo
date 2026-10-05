package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class ReservationLightweightResponse(
    val reservationByIdList: List<LightweightReservationById> = emptyList(),
)

@Serializable
data class LightweightReservationById(
    val reservationId: String? = null,
    val hotelId: String? = null,
    val checkInTime: String? = null,
    val checkOutTime: String? = null,
    val email: String? = null,
    val purposeOfStay: String? = null,
    val reservationPackageList: List<LightweightReservationPackage> = emptyList(),
)

@Serializable
data class LightweightReservationPackage(
    val packageCode: String? = null,
    val description: String? = null,
    val unitPrice: Double? = null,
    val totalQuantity: Int? = null,
    val packageGroup: String? = null,
    val computedPrice: Double? = null,
    val grossPrice: Double? = null,
    val vatTax: Double? = null,
    val startDate: String? = null,
    val endDate: String? = null,
)
