package com.whitbread.premierinn.hoteldetails.analytics

import com.whitbread.premierinn.common.analytics.toHotelProductRoomTracking
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.utils.StringUtils.toColonSeparatedString
import com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain
import com.whitbread.premierinn.hoteldetails.RatesDisplayInfo
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.EVENT_HOTEL_AVAILABLE
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.EVENT_HOTEL_NOT_AVAILABLE
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.SEARCH_TYPE_HOTEL_SPECIFIC
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.SEARCH_TYPE_LOCATION
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.SEARCH_TYPE_MAP
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData.SEARCH_TYPE_NEAR_ME
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.Locale

private const val EVAR_65 = "eVar65"
private const val BETWEEN_RATES_EVAR_DELIMITER = ">"
fun toProductString(hotelAvailabilityDomain: HotelAvailabilityDomain, hotelCode: String,
                    rateDisplayInfoList: List<RatesDisplayInfo>): String {
    return toHotelProductRoomTracking(hotelCode) +
            getAvailabilityEvent(hotelAvailabilityDomain) + ";" +
            addRatePlanEventIfAvailable(rateDisplayInfoList)
}

fun getAvailabilityEvent(hotelAvailabilityDomain: HotelAvailabilityDomain) : String {
    return if (hotelAvailabilityDomain.available) {
        EVENT_HOTEL_AVAILABLE
    } else {
        EVENT_HOTEL_NOT_AVAILABLE
    }
}

fun addRatePlanEventIfAvailable(rateDisplayInfoList: List<RatesDisplayInfo>): String {
    var productStringBuilder = EMPTY_STRING_DOMAIN
    for (ratedisplay in rateDisplayInfoList) {
        if (productStringBuilder.isNotEmpty()) {
            productStringBuilder += BETWEEN_RATES_EVAR_DELIMITER
        }
        productStringBuilder += TrackingAnalyticsUtils.formatRateInfo(ratedisplay.rateCode,
                ratedisplay.lettingType, ratedisplay.price)
    }
    return "${EVAR_65}=" + productStringBuilder
}

fun getSearchType(cameFromMapView: Boolean, placeName: String?, hotelCode: String?): String {
    return if (cameFromMapView) {
        SEARCH_TYPE_MAP
    } else if (placeName != null && placeName == "My Location") {
        SEARCH_TYPE_NEAR_ME
    } else if (!hotelCode.isNullOrEmpty()) {
        SEARCH_TYPE_HOTEL_SPECIFIC
    } else if (hotelCode == null || placeName == null){
        SEARCH_TYPE_LOCATION
    } else {
        SEARCH_TYPE_LOCATION
    }
}

fun String.toLocalDate(): LocalDate {
    val dateTimeFormatter = DateTimeFormatter.ofPattern(DateFormat.DASHED_YEAR_MONTH_DAY)
    return LocalDate.parse(this, dateTimeFormatter)
}

fun String.toSlashedDate(): String {
    val dashedFormatter = DateTimeFormatter.ofPattern(DateFormat.DASHED_YEAR_MONTH_DAY)
    val slashedFormatter = DateTimeFormatter.ofPattern(DateFormat.SLASHED_DAY_MONTH_YEAR)
    return LocalDate.parse(this, dashedFormatter).format(slashedFormatter)
}

fun HotelAvailabilityDomain.getListOfRoom(): String {
    val listOfRoomTypes = mutableListOf<String>()
    this.roomRateDomainList?.first()?.roomTypesDomainList?.forEach { roomType ->
        listOfRoomTypes.add(roomType.roomType)
    }

    return  toColonSeparatedString(listOfRoomTypes)
}

fun getStartEndDateString(arrivalDate: String, departureDate:String): String {
    val dateTimeFormatter = DateTimeFormatter.ofPattern(DateFormat.DASHED_YEAR_MONTH_DAY)

    val arrivalDateLD = LocalDate.parse(arrivalDate, dateTimeFormatter)
    val departureDateLD = LocalDate.parse(departureDate, dateTimeFormatter)

    return  getWeekDay(arrivalDateLD) + "-" + getWeekDay(departureDateLD)
}

fun getWeekDay(date: LocalDate) : String {
    return date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.UK)
}
