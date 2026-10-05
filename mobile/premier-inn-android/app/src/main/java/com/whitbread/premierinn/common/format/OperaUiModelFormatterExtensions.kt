package com.whitbread.premierinn.common.format

import com.whitbread.premierinn.R
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelFacilityDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain
import com.whitbread.premierinn.domain.common.toFacility2Type
import com.whitbread.premierinn.domain.hotel.entity.Facility2

fun List<HotelFacilityDomain>.withIcons(): List<Pair<Int, String>> {
    return this.map { it.code.toFacility2Type().mapToPair(it) }.toList()
}

fun Facility2.Type.mapToPair(hotelFacilityDomain: HotelFacilityDomain): Pair<Int, String> {
    return when (this) {
        Facility2.Type.FREE_PARKING -> Pair(R.drawable.ic_free_parking, hotelFacilityDomain.description)
        Facility2.Type.CHARGEABLE_PARKING -> Pair(R.drawable.ic_chargeable_parking, hotelFacilityDomain.description)
        Facility2.Type.CHARGEABLE_ONSITE_PARKING -> Pair(R.drawable.ic_chargeable_parking, hotelFacilityDomain.description)
        Facility2.Type.CHARGEABLE_OFFSITE_PARKING-> Pair(R.drawable.ic_chargeable_parking, hotelFacilityDomain.description)
        Facility2.Type.FREE_WIFI -> Pair(R.drawable.ic_wifi, hotelFacilityDomain.description)
        Facility2.Type.ACCESSIBLE_ROOM -> Pair(R.drawable.ic_accessibility, hotelFacilityDomain.description)
        Facility2.Type.LIFT -> Pair(R.drawable.ic_lift, hotelFacilityDomain.description)
        Facility2.Type.FAMILY -> Pair(R.drawable.ic_family, hotelFacilityDomain.description)
        Facility2.Type.AIR_CONDITIONING -> Pair(R.drawable.ic_aircon, hotelFacilityDomain.description)
        Facility2.Type.RESTAURANT -> Pair(R.drawable.ic_restaurant, hotelFacilityDomain.description)
        Facility2.Type.LUGGAGE_STORAGE -> Pair(R.drawable.ic_luggage_storage, hotelFacilityDomain.description)
        Facility2.Type.SLEEP_PARK_FLY -> Pair(R.drawable.ic_sleep_park_fly, hotelFacilityDomain.description)
        Facility2.Type.MEETING_ROOM -> Pair(R.drawable.ic_meeting_room, hotelFacilityDomain.description)
        Facility2.Type.IN_ROOM_APP -> Pair(R.drawable.ic_room_controls, hotelFacilityDomain.description)
        Facility2.Type.COSTA -> Pair(R.drawable.ic_costa, hotelFacilityDomain.description)
        Facility2.Type.UNKNOWN -> Pair(R.drawable.ic_tick_facility, hotelFacilityDomain.description)
        else -> throw IllegalArgumentException(" Facility Type ${this.name} is not supported")
    }
}

fun HotelInformationDomain.parkingPair(): Pair<Int, String>? {
    if (this.getListOfParkingCodes().isNotEmpty()) {
        this.getListOfParkingCodes().forEach {
            return it.code.toFacility2Type().mapToPair(it)
        }
    }
    return null
}
