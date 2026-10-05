package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class HotelDonationPackagesResponse(
    val donationPackages: List<HotelDonationPackage> = emptyList(),
)

@Serializable
data class HotelDonationPackage(
    val code: String? = null,
    val unitPrice: Double? = null,
    val currency: String? = null,
)
