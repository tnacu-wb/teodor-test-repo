package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class HotelPackagesResponse(
    val restaurant: HotelPackagesRestaurant? = null,
    val packages: HotelPackages? = null,
    val hotelHasCityTaxForLeisure: Boolean? = null,
    val hotelHasCityTaxForBusiness: Boolean? = null,
)

@Serializable
data class HotelPackagesRestaurant(
    val logoUrl: String? = null,
    val restaurantNotFound: Boolean? = null,
    val noMealsFound: Boolean? = null,
)

@Serializable
data class HotelPackages(
    val meals: List<HotelPackageMeal> = emptyList(),
)

@Serializable
data class HotelPackageMeal(
    val title: String? = null,
    val id: String? = null,
    val price: Double? = null,
    val idImg: String? = null,
    val idDesc: String? = null,
    val allergyInfoUrl: String? = null,
    val currency: String? = null,
    val basePrice: Double? = null,
    val isFree: Boolean? = null,
    val inventoryItem: String? = null,
)
