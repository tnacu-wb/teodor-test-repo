package com.whitbread.premierinn.hoteldetails

import android.os.Parcelable
import com.whitbread.premierinn.common.ParcelableDailyRate
import com.whitbread.premierinn.common.ParcelablePrice
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookingRoomOpera(
        val roomNumber: Int,
        val dailyRates: List<ParcelableDailyRate>,
        val type: String,
        val pmsRoomType: String,
        val adults: Int,
        val children: Int,
        val cot: Boolean,
        val totalCost: ParcelablePrice,
        val cityTax: ParcelablePrice?,
        val lettingType: String,
        val alternativeRooms: List<BookingRoomOpera>?
) : Parcelable
