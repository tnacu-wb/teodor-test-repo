package com.whitbread.premierinn.data.hotel.mapper

import com.whitbread.premierinn.domain.hotel.entity.Hotel
import java.util.Locale

fun mapToBrand(value: String?): Hotel.Brand {
    return when (value?.uppercase(Locale.getDefault())) {
        "HUB" -> Hotel.Brand.HUB
        "PI" -> Hotel.Brand.PI
        "PID" -> Hotel.Brand.PID
        "ZIP" -> Hotel.Brand.ZIP
        else -> Hotel.Brand.UNKNOWN

    }
}

fun mapHotelBrandToBrandString(value: Hotel.Brand?): String {
    return when (value) {
        Hotel.Brand.HUB -> "HUB"
        Hotel.Brand.PI -> "PI"
        Hotel.Brand.PID -> "PID"
        Hotel.Brand.ZIP -> "ZIP"
        else -> "UNKNOWN"

    }
}