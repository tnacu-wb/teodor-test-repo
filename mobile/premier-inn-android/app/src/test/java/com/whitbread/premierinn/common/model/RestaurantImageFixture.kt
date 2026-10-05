package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.domain.hotel.entity.Hotel

object RestaurantImageFixture {
    fun aRestaurantImage(
        fileReference : String = "/content/dam/pi/websites/hotelimages/gb/en/L/LONBLE/London_Bexleyheath_02.jpg"
    ) : Hotel.RestaurantImage {
        return Hotel.RestaurantImage(
                fileReference = fileReference
        )
    }
}