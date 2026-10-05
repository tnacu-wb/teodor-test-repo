package com.whitbread.premierinn.alternativeroomselection.analytics

import com.whitbread.premierinn.common.analytics.getEvent94ProductCode
import com.whitbread.premierinn.common.analytics.toHotelProductRoomTracking
import com.whitbread.premierinn.common.utils.sumByFloat
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.TwinTypeCode
import com.whitbread.premierinn.domain.common.UNDERSCORE_STRING_DOMAIN
import com.whitbread.premierinn.hoteldetails.BookingRoomOpera
import com.whitbread.premierinn.summary.SummaryInput

private const val EVAR_65 = "eVar65"

fun toAlternativeRoomTracking(summaryInput: SummaryInput, listOfOriginalRooms: List<BookingRoomOpera>): String {
    return toHotelProductRoomTracking(summaryInput.hotel().code()) +
            getEvent94ProductCode() +
            getEVar62ProductCode(listOfOriginalRooms, formatRateCode(summaryInput.rate().code()))
}

fun getEVar62ProductCode(listOfOriginalRooms: List<BookingRoomOpera>, rateCode: String): String {
    return "${EVAR_65}=" + listOfOriginalRooms.toFirstOptionsTracking(rateCode) +
            listOfOriginalRooms.toSecondOptionsTracking(rateCode)
}

fun List<BookingRoomOpera>.toFirstOptionsTracking(rateCode: String): String {
    return if (this.isNotEmpty()) {
        var firstOptions: String = EMPTY_STRING_DOMAIN
        val totalPriceTrackingData = this.sumByFloat { it.totalCost.amount }
        val pmsRoomTypeTrackingData = this.joinToString(UNDERSCORE_STRING_DOMAIN) { it.pmsRoomType }

        firstOptions += "$rateCode-"
        firstOptions += "$pmsRoomTypeTrackingData-"
        firstOptions += totalPriceTrackingData

        return firstOptions
    } else EMPTY_STRING_DOMAIN
}

fun List<BookingRoomOpera>.toSecondOptionsTracking(rateCode: String): String {
    return if (this.isNotEmpty()) {
        var secondOptions: String = EMPTY_STRING_DOMAIN
        var totalPriceTrackingData = 0.0f
        val listOfpmsRoomType = mutableListOf<String>()

        this.map {
            if (TwinTypeCode(it.lettingType).twinRoomType != null && !it.alternativeRooms.isNullOrEmpty()) {
                it.alternativeRooms[0].pmsRoomType?.let { pmsRoomType ->
                    listOfpmsRoomType.add(pmsRoomType)
                }
                it.alternativeRooms[0].totalCost?.let { bookingPrice ->
                    totalPriceTrackingData += bookingPrice.amount
                }
            } else {
                totalPriceTrackingData += it.totalCost.amount
                listOfpmsRoomType.add(it.pmsRoomType)
            }
        }

        secondOptions += "$rateCode-"
        secondOptions += "${listOfpmsRoomType.joinToString(UNDERSCORE_STRING_DOMAIN)}-"
        secondOptions += totalPriceTrackingData

        if (secondOptions.isNotEmpty()) {
            secondOptions = ">$secondOptions"
        }

        return secondOptions
    } else EMPTY_STRING_DOMAIN
}

fun formatRateCode(rateCode: String): String {
    return rateCode.replace("[0-9]".toRegex(), "")
}