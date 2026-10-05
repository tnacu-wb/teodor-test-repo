package com.whitbread.premierinn.common.format

import com.whitbread.premierinn.R
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.hotel.entity.Facility2
import com.whitbread.premierinn.domain.hotel.entity.Hotel

/**
 *
 */
fun Address.formatted(): String {
    return listOf(this.companyName, this.line1, this.line2, this.line3, this.line4, this.postCode)
            .filter { !it.isNullOrEmpty() }
            .joinToString { it.toString() }
}

fun List<Facility2>.withIcons(): List<Pair<Int, String>> {
    return this.map { it.mapToPair() }.toList()
}

fun Facility2.mapToPair(): Pair<Int, String> {
    return when (this.type) {
        Facility2.Type.FREE_PARKING -> Pair(R.drawable.ic_free_parking, this.description)
        Facility2.Type.CHARGEABLE_PARKING -> Pair(R.drawable.ic_chargeable_parking, this.description)
        Facility2.Type.CHARGEABLE_ONSITE_PARKING -> Pair(R.drawable.ic_chargeable_parking, this.description)
        Facility2.Type.FREE_WIFI -> Pair(R.drawable.ic_wifi, this.description)
        Facility2.Type.ACCESSIBLE_ROOM -> Pair(R.drawable.ic_accessibility, this.description)
        Facility2.Type.LIFT -> Pair(R.drawable.ic_lift, this.description)
        Facility2.Type.FAMILY -> Pair(R.drawable.ic_family, this.description)
        Facility2.Type.AIR_CONDITIONING -> Pair(R.drawable.ic_aircon, this.description)
        Facility2.Type.RESTAURANT -> Pair(R.drawable.ic_restaurant, this.description)
        Facility2.Type.LUGGAGE_STORAGE -> Pair(R.drawable.ic_luggage_storage, this.description)
        Facility2.Type.SLEEP_PARK_FLY -> Pair(R.drawable.ic_sleep_park_fly, this.description)
        Facility2.Type.MEETING_ROOM -> Pair(R.drawable.ic_meeting_room, this.description)
        Facility2.Type.IN_ROOM_APP -> Pair(R.drawable.ic_room_controls, this.description)
        Facility2.Type.COSTA -> Pair(R.drawable.ic_costa, this.description)
        else -> throw IllegalArgumentException(" Facility Type ${this.type} not supported")
    }
}

fun Hotel.parkingPair(): Pair<Int, String>? {
    listOf(Facility2.Type.FREE_PARKING, Facility2.Type.CHARGEABLE_PARKING, Facility2.Type.CHARGEABLE_ONSITE_PARKING)
            .forEach {
                val facility = this.has(it)
                if (facility != null) return facility.mapToPair() else return@forEach
            }
    return null
}

fun Guest.nameFormatted(): String? {
    return if (firstName != null && lastName != null) {
        "$title $firstName $lastName"
    } else {
        null
    }

}