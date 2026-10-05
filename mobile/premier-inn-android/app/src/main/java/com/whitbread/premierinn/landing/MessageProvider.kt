package com.whitbread.premierinn.landing

import android.content.res.Resources
import com.whitbread.premierinn.R
import com.whitbread.premierinn.domain.common.toGermanIfApplicable
import com.whitbread.premierinn.domain.dashboard.entity.Room
import java.util.*

class MessageProvider(val resources: Resources) {

    fun currentLocation() = resources.getString(R.string.current_location)

    fun todaysArrivalOneNight() = resources.getString(R.string.landing_todays_arrival_one_night)

    fun todaysArrivalMultipleNights(departure: String) = resources
            .getString(R.string.landing_todays_arrival_multiple_nights, departure)

    fun arrivalAndDeparture(arrival: String, departure: String) = resources
            .getString(R.string.landing_screen_date_text, arrival, departure)

    fun guestsAndRooms(guests: Int, rooms: Int) = String.format("%s, %s",
            resources.getQuantityString(R.plurals.guests, guests, guests),
            resources.getQuantityString(R.plurals.rooms, rooms, rooms))

    fun adultsAndRooms(adults: Int, rooms: Int) = String.format("%s, %s",
            resources.getQuantityString(R.plurals.number_of_adults, adults, adults),
            resources.getQuantityString(R.plurals.rooms, rooms, rooms))

    fun formatGuestAndRooms(rooms: List<Room>, numberOfGuests: Int, numberOfRooms: Int,
                            allRoomsAreTheSame: Boolean, language: String): String {
        return if (rooms.isNotEmpty() && allRoomsAreTheSame) {
            resources.getString(
                    R.string.guests_and_rooms_type,
                    resources.getQuantityString(R.plurals.guests, numberOfGuests, numberOfGuests),
                    resources.getQuantityString(R.plurals.type_rooms, numberOfRooms,
                            numberOfRooms, rooms.first().type.lowercase(Locale.getDefault()).toGermanIfApplicable(language))
            )
        } else {
            resources.getString(
                    R.string.guests_and_rooms,
                    resources.getQuantityString(R.plurals.guests, numberOfGuests, numberOfGuests),
                    resources.getQuantityString(R.plurals.rooms, numberOfRooms, numberOfRooms)
            )
        }
    }

    fun upcomingBookingCheckedIn() =  resources.getString(R.string.checked_in)
}