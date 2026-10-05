package com.whitbread.premierinn.ciol.analytics

import com.whitbread.premierinn.data.common.EMPTY_STRING

data class CiolAnalyticsModel @JvmOverloads constructor(
    val bookingId: String = EMPTY_STRING,
    val action: String = EMPTY_STRING,
    val checkInDate: String = EMPTY_STRING,
    val checkOutDate: String = EMPTY_STRING,
    val checkInDay: String = EMPTY_STRING,
    val isDeepLinked: Boolean = false,
    val pushToken: String = EMPTY_STRING,
    val checkOutDay: String = EMPTY_STRING,
    val checkInOutDay: String = EMPTY_STRING,
    val noNights: String = EMPTY_STRING,
    val noRooms: String = EMPTY_STRING,
    val noAdults: String = EMPTY_STRING,
    val noChildren: String = EMPTY_STRING,
    val extraDescription: String = EMPTY_STRING,
    val hotelId: String = EMPTY_STRING,
    val rateCode: String = EMPTY_STRING,
    val rateDescription: String = EMPTY_STRING,
    val rateName: String = EMPTY_STRING,
    val errorMessage: String = EMPTY_STRING,
)
