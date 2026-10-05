package com.whitbread.premierinn.common.analytics

private const val EVENT_94 = "event94"

fun toHotelProductRoomTracking(hotelCode: String): String {
    return ";$hotelCode;;;"
}

fun getEvent94ProductCode(): String {
    return "${EVENT_94}=1;"
}