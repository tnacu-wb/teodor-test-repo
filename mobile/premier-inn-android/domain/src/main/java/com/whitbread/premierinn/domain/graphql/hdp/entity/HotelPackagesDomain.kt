package com.whitbread.premierinn.domain.graphql.hdp.entity

// Need to rename this to HotelPackagesDomain later

data class DataPackagesDomain (
    val restaurant: RestaurantDomain,
    val packages: PackagesPackagesDomain,
    val hotelHasCityTaxForLeisure: Boolean,
    val hotelHasCityTaxForBusiness : Boolean
){
    companion object {
        fun createDefault(): DataPackagesDomain {
            return DataPackagesDomain(
                restaurant = RestaurantDomain(
                    noMealsFound = false,
                    messageDescription = null,
                    restaurantNotFound = false,
                    menus = emptyList()
                ),
                packages = PackagesPackagesDomain(
                    meals = emptyList(),
                    mealsKids = emptyList(),
                    extrasItems = null,
                    roomSelection = null
                ),
                hotelHasCityTaxForLeisure = false,
                hotelHasCityTaxForBusiness = false
            )
        }
    }
}

sealed class UpsellDomainItem

data class PackagesPackagesDomain (
    val meals: List<MealDomain>,
    val mealsKids: List<MealDomain>,
    val extrasItems: List<ExtrasItemDomain>?,
    val roomSelection: List<RoomSelectionDomain>?
)

data class MealDomain (
    val id: String? = null,
    val name: String,
    val bartId: String? = null,
    val price: Double? = null,
    val totalPrice: Double? = null,
    val freeBreakfastOption: Boolean? = null,
    val freeBreakfastCode: String? = null,
    val description: String? = null,
    val shortDescription: String? = null,
    val imageSrc: String? = null,
    val allergyInfoSrc: String? = null,
    val currency: String? = null,
    val order: Int? = null,
    val menu: MenuDomain? = null,
    var preselectedFreeBfSelections: Int = 0,
    var preselectedNoOfSelections: Int = 0,
    val numberOfSelections: Int = 0,
    val freeBreakfastSelections: Int = 0,
) : UpsellDomainItem()

data class ExtrasItemDomain(
    val id: String,
    val name: String? = null,
    val price: Double? = null,
    val description: String? = null,
    val imageSrc: String? = null,
    val currency: String? = null,
    val order: Int? = null,
    val available: Int? = null,
    val formattedPrice: String? = null,
    var preselectedNoOfSelections: Int = 0,
    var numberOfSelections: Int = 0,
) : UpsellDomainItem()

data class RoomSelectionDomain (
    val reservationId: String? = null,
    val packagesSelection: List<PackagesSelectionDomain>
)

data class PackagesSelectionDomain (
    val id: String? = null,
    var noOfSelections: Int
)

data class RestaurantDomain (
    val noMealsFound: Boolean,
    val messageDescription: String? = null,
    val restaurantNotFound: Boolean,
    val menus: List<MenuDomain>
)

data class MenuDomain (
    val name: String,
    val menuSrc: String
)

