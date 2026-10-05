package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.domain.common.hoteldetails.entity.AncillaryCloseOutItem
import com.whitbread.premierinn.domain.graphql.amend.entity.PackagesAndAncillaryCloseoutDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.MealDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.MenuDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.PackagesPackagesDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.PackagesSelectionDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RestaurantDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomSelectionDomain

object PackagesAndAncillaryCloseoutDomainFixture {

    fun aPackagesAndAncillaryCloseoutDomain(
        packages: DataPackagesDomain = DataPackagesDomain.createDefault(),
        ancillaryCloseOutItems: List<AncillaryCloseOutItem> = emptyList()
    ): PackagesAndAncillaryCloseoutDomain {
        return PackagesAndAncillaryCloseoutDomain(
            packages = packages,
            ancillaryCloseOutItems = ancillaryCloseOutItems
        )
    }

    fun createDataPackagesDomain() = DataPackagesDomain(
        restaurant = RestaurantDomain(
            noMealsFound = false,
            messageDescription = null,
            restaurantNotFound = false,
            menus = emptyList()
        ),
        packages = createPackagesPackagesDomain(),
        hotelHasCityTaxForLeisure = false,
        hotelHasCityTaxForBusiness = false
    )

    fun createPackagesPackagesDomain() = PackagesPackagesDomain(
        meals = listOf(premierInnBreakfast, continentalBreakfast),
        mealsKids = listOf(mealForKids),
        extrasItems = listOf(ultimateWiFi),
        roomSelection = null
    )


    val premierInnBreakfast = MealDomain(
        id = "BFADBF",
        bartId = "11",
        name = "Premier Inn Breakfast",
        price = 11.99,
        freeBreakfastOption = true,
        freeBreakfastCode = "BFCHDF",
        description = "<p>Add our unlimited breakfast and look forward to freshly cooked bacon, fluffy hash browns, succulent sausages, eggs the way you like them, cereals, fresh fruit, croissants and much more.</p>\r\n",
        imageSrc = "/content/dam/global/restaurants/Global/full-english-vegan-bookingflow.jpg",
        allergyInfoSrc = "/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-breakfast.pdf",
        currency = "GBP",
        order = 1,
        menu = MenuDomain(
            name = "Breakfast menu",
            menuSrc = "/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf"
        )
    )

    val continentalBreakfast = MealDomain(
        id = "BFADCT",
        bartId = "12",
        name = "Continental Breakfast",
        price = 9.0,
        freeBreakfastOption = false,
        freeBreakfastCode = "BFCHDF",
        description = "<p>A lighter start with tasty pastries, American pancakes, fruit and cereals. Includes smoothies and juices.</p>\r\n",
        imageSrc = "/content/dam/global/restaurants/Global/pi-breakfast-tacos-booking.jpg",
        allergyInfoSrc = "/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-breakfast.pdf",
        currency = "GBP",
        order = 2,
        menu = MenuDomain(
            name = "Breakfast menu",
            menuSrc = "/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf"
        )
    )

    val mealForKids = MealDomain(
        id = "BFCHDF",
        name = "Free breakfast for kids",
        price = null,
        description = "<p>Up to two kids eat breakfast for free when an adult orders a Premier Inn Breakfast or Meal Deal.</p>\n",
        imageSrc = "/content/dam/global/restaurants/Global/child-breakfast.jpg",
        allergyInfoSrc = "/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-breakfast.pdf",
        currency = null,
        order = 0,
        menu = null
    )

    val ultimateWiFi = ExtrasItemDomain(
        name = "Ultimate Wi-Fi",
        id = "FI24HR",
        price = 5.0,
        imageSrc = "/content/dam/global/extras/ultimate-wifi.png",
        description = "<p>Download files faster, stream movies, make video calls and browse with ease with our Ultimate Wi-Fi package.</p>\r\n",
        currency = "GBP",
        order = 1,
        available = null
    )

    val roomSelection = RoomSelectionDomain(
        packagesSelection = listOf(
            PackagesSelectionDomain(
                id = "BFCHDF",
                noOfSelections = 2
            ),
            PackagesSelectionDomain(
                id = "BFADBF",
                noOfSelections = 2
            )
        )
    )
}



