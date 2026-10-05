package com.whitbread.premierinn.searchresults

import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.view.ListItem
import com.whitbread.premierinn.domain.common.Availability
import com.whitbread.premierinn.domain.common.TripAdvisorRating
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import com.whitbread.premierinn.domain.search.entity.Location

/**
 *
 */
data class HotelListItem(val name: String,
                         val code: String,
                         val brand: Hotel.Brand,
                         val location: Location,
                         val distance: Double,
                         val formattedDistance: String,
                         val imagePath: String,
                         val flag: Hotel.Flag?,
                         val pmsSource: String,
                         val cellCode: String,
                         val parkingDrawableDescription: Pair<Int, String>?,
                         val availability: Availability,
                         val tripAdvisorRating: TripAdvisorRating?,
                         val priceFrom: String,
                         val fullyBookedText: String?,
                         val type: Int) : ListItem {

    val limitedAvailability = availability == Availability.LIMITED
    val fullyBooked = availability == Availability.SOLD_OUT

    override fun id(): String = code
    override fun type(): Int = type
}

data class FullyBookedHotelListItem(val name: String, val code: String, val imagePath: String?) : ListItem {
    override fun id(): String = code.plus(type())
    override fun type(): Int = R.layout.item_search_results_hotel_fully_booked
}

object LoadingListItem : ListItem {
    override fun id(): String = type().toString()
    override fun type(): Int = R.layout.view_hotel_details_rate_loading
    override fun equals(other: Any?): Boolean {
        return other is LoadingListItem
    }
}

data class ErrorItem(val errorResId: Int? = null) : ListItem {
    override fun id(): String = type().toString().plus(errorResId)
    override fun type(): Int = R.layout.item_search_results_hotel_error_message
}


data class BookingCriteriaUi(val searchItemName: String,
                             val searchedLocation: Location,
                             val bookingCriteriaFormatted: String)