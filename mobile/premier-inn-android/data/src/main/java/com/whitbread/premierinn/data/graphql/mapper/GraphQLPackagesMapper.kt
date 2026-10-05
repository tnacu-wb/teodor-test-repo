package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.PackagesGraphQLContract
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.MealDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.MenuDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.PackagesPackagesDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.PackagesSelectionDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RestaurantDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomSelectionDomain

const val ULTIMATE_WIFI_24_HRS = "FI24HR"

fun PackagesGraphQLContract.Packages?.mapToPackagesGraphQL(numberOfNights: Int): DataPackagesDomain {
    return this?.let {
         DataPackagesDomain(
            restaurant = this.restaurant.toDomain(),
            packages = this.toDomain(numberOfNights),
            hotelHasCityTaxForLeisure = this.hotelHasCityTaxForLeisure,
            hotelHasCityTaxForBusiness = this.hotelHasCityTaxForBusiness
        )
    } ?: DataPackagesDomain.createDefault()
}

    fun PackagesGraphQLContract.Packages?.toDomain(numberOfNights: Int? = null): PackagesPackagesDomain {
        return if (this != null && this.packages != null) {
            PackagesPackagesDomain(
                meals = this.packages.meals.toMealsListDomain(),
                mealsKids = this.packages.mealsKids.toMealsListDomain(),
                roomSelection = this.packages.roomSelection?.toListOfRoomSelectionDomain(),
                extrasItems = this.packages.extrasItems?.toListOfExtrasItemDomain(numberOfNights)
            )
        } else {
            PackagesPackagesDomain(
                meals = emptyList(),
                mealsKids = emptyList(),
                roomSelection = null,
                extrasItems = null
            )
        }
    }

    fun PackagesGraphQLContract.Packages?.toDataPackagesDomain(): DataPackagesDomain {
    return if (this != null && this.packages != null) {
        DataPackagesDomain(
            hotelHasCityTaxForLeisure = this.hotelHasCityTaxForLeisure,
            hotelHasCityTaxForBusiness = this.hotelHasCityTaxForBusiness,
            restaurant = this.restaurant.toDomain(),
            packages = this.toDomain(),
        )
    } else {
        DataPackagesDomain(
            hotelHasCityTaxForLeisure = false,
            hotelHasCityTaxForBusiness = false,
            restaurant = RestaurantDomain(
                noMealsFound = false,
                messageDescription = EMPTY_STRING_DOMAIN,
                restaurantNotFound = false,
                menus = emptyList()
            ),
            packages = PackagesPackagesDomain(
                meals = emptyList(),
                mealsKids = emptyList(),
                roomSelection = null,
                extrasItems = null
            )
        )
    }
}

    fun PackagesGraphQLContract.Restaurant.toDomain(): RestaurantDomain {
        return RestaurantDomain(
            noMealsFound = this.noMealsFound,
            messageDescription = messageDescription ?: EMPTY_STRING_DOMAIN,
            restaurantNotFound = restaurantNotFound,
            menus = menus?.toMenuListDomain() ?: emptyList()
        )
    }

    fun List<PackagesGraphQLContract.Meal>?.toMealsListDomain(): List<MealDomain> {
        val meals = mutableListOf<MealDomain>()
        this?.forEach {
            meals.add(
                MealDomain(
                    id = it.id,
                    name = it.name ?: EMPTY_STRING_DOMAIN,
                    bartId = it.bartId,
                    price = it.price ?: 0.00,
                    totalPrice = it.totalPrice ?: 0.00,
                    freeBreakfastOption = it.freeBreakfastOption ?: false,
                    freeBreakfastCode = it.freeBreakfastCode ?: EMPTY_STRING_DOMAIN,
                    description = it.description ?: EMPTY_STRING_DOMAIN,
                    shortDescription = it.shortDescription ?: EMPTY_STRING_DOMAIN,
                    imageSrc = it.imageSrc ?: EMPTY_STRING_DOMAIN,
                    allergyInfoSrc = it.allergyInfoSrc ?: EMPTY_STRING_DOMAIN,
                    currency = it.currency ?: GBP,
                    order = it.order ?: 0,
                    menu = it.menu?.toMenuDomain()
                )
            )
        }

        return meals
    }

    fun List<PackagesGraphQLContract.Menu>.toMenuListDomain(): List<MenuDomain> {
        val menuList = mutableListOf<MenuDomain>()
        forEach {
            menuList.add(
                MenuDomain(
                    name = it.name,
                    menuSrc = it.menuSrc
                )
            )
        }

        return menuList
    }

    fun PackagesGraphQLContract.Menu.toMenuDomain() = MenuDomain(
        name = name,
        menuSrc = menuSrc
    )


fun List<PackagesGraphQLContract.RoomSelection>.toListOfRoomSelectionDomain(): List<RoomSelectionDomain> {
    val listOfRoomSelection = mutableListOf<RoomSelectionDomain>()

    this.forEach { roomSelection ->
        listOfRoomSelection.add(RoomSelectionDomain(
            reservationId = roomSelection.reservationId,
            packagesSelection = roomSelection.packagesSelection.toListOfPackagesSelectionDomain()))
    }

    return listOfRoomSelection
}

fun List<PackagesGraphQLContract.ExtrasItem>.toListOfExtrasItemDomain(numberOfNights: Int? = null): List<ExtrasItemDomain> {
    return this.map { extrasItems ->
        ExtrasItemDomain(
            currency = extrasItems.currency,
            description = extrasItems.description,
            id = extrasItems.id,
            imageSrc = extrasItems.imageSrc,
            name = extrasItems.name,
            order = extrasItems.order,
            price = if (extrasItems.id == ULTIMATE_WIFI_24_HRS && numberOfNights != null) extrasItems.price?.div(numberOfNights) else extrasItems.price,
            // set dummy value for ultimate wifi as we do some checks for available, however it is not actually used for ultimate wifi
            available = if (extrasItems.id == ULTIMATE_WIFI_24_HRS) 100 else extrasItems.available
        )
    }
}

fun List<PackagesGraphQLContract.PackagesSelection>.toListOfPackagesSelectionDomain(): List<PackagesSelectionDomain> {
    val listOfPackagesSelection = mutableListOf<PackagesSelectionDomain>()

    this.forEach { packagesSelection ->
        listOfPackagesSelection.add(PackagesSelectionDomain(
            id = packagesSelection.id,
            noOfSelections = packagesSelection.noOfSelections))
    }

    return listOfPackagesSelection
}
