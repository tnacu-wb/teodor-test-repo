package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.TripAdvisorRating
import com.whitbread.premierinn.domain.hotel.entity.AcceptedCreditCard
import com.whitbread.premierinn.domain.hotel.entity.CityTax
import com.whitbread.premierinn.domain.hotel.entity.Facility2
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import com.whitbread.premierinn.domain.search.entity.Location

object HotelFixture {

    fun aHotel(
         code: String = "LONMON",
         name: String = "London KingsCross",
         address: Address = Address(""),
         location: Location = Location(1.0, 1.0),
         brand: Hotel.Brand = Hotel.Brand.PI,
         cityTax: CityTax? = null,
         contactDetails: Hotel.ContactDetails? = null,
         facilities: List<Facility2> = emptyList(),
         photoPath: String? = null,
         restaurantImage: List<Hotel.RestaurantImage>? = mutableListOf(RestaurantImageFixture.aRestaurantImage()),
         flag: Hotel.Flag? = null,
         rating: TripAdvisorRating? = null,
         roomVariantDetails: List<Hotel.RoomVariantDetails> = emptyList(),
         acceptedCreditCards: List<AcceptedCreditCard> = emptyList()) : Hotel {
        return Hotel(
                code,
                name,
                address,
                location,
                brand,
                cityTax,
                contactDetails,
                facilities,
                photoPath,
                restaurantImage,
                flag,
                rating,
                roomVariantDetails,
                acceptedCreditCards
        )
    }
}