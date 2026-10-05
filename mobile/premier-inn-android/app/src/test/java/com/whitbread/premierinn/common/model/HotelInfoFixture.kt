package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.TopSectionImageDomain

object HotelInfoFixture {

    fun aHotelInfoMockForNameIncludingRestaurantTopSectionImage() : HotelInformationDomain {
        val listOfTopSectionImageDomain = mutableListOf<TopSectionImageDomain>()
        val topSectionImageOne = TopSectionImageDomain(
            imageSrc = "/content/dam/pi/websites/hotelimages/gb/en/B/BANBRI/BANBRI 1.jpg",
            tags = listOf( "exterior", "parking"))
        val topSectionImageTwo = TopSectionImageDomain(
            imageSrc = "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Restaurants/Table Table/Table Table 1.jpg",
            tags = listOf( "table-table", "restaurant"))
        listOfTopSectionImageDomain.add(topSectionImageOne)
        listOfTopSectionImageDomain.add(topSectionImageTwo)
        return HotelInformationDomain.createEmptyDomain().copy(
            name = "Bangor",
            topSectionImages = listOfTopSectionImageDomain)
    }
}