package com.whitbread.premierinn.searchresults

import androidx.annotation.DrawableRes
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.format.format
import com.whitbread.premierinn.common.format.formatted
import com.whitbread.premierinn.common.view.ListItem
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.common.Availability
import com.whitbread.premierinn.domain.common.Availability.LIMITED
import com.whitbread.premierinn.domain.common.Availability.OK
import com.whitbread.premierinn.domain.common.Availability.SOLD_OUT
import com.whitbread.premierinn.domain.common.BookingCriteria
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.MILES_TO_KM
import com.whitbread.premierinn.domain.common.mapToBrand
import com.whitbread.premierinn.domain.common.toAvailability
import com.whitbread.premierinn.domain.common.toFacility2Type
import com.whitbread.premierinn.domain.common.toFlag
import com.whitbread.premierinn.domain.common.toLocation
import com.whitbread.premierinn.domain.common.toPriceDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.FacilityItemDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.LowestRoomRateDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.ShortHotelInformationDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.SingleHotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.srp.entity.ThumbnailImageDomain
import com.whitbread.premierinn.domain.hotel.entity.Facility2
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import io.reactivex.functions.Function
import java.util.Locale
import javax.inject.Inject

/**
 *
 */
class HotelItemMapper @Inject constructor(
    private val stringProvider: StringResourceProvider,
    private val deviceLocaleProvider: DeviceLocaleProvider) : Function<SingleHotelAvailabilityDomain, ListItem> {
    override fun apply(it: SingleHotelAvailabilityDomain): ListItem {
        return HotelListItem(name = it.name,
            brand = mapToBrand(it.hotelInformation.brand),
            code = it.hotelId,
            location = it.hotelInformation.coordinates.toLocation(),
            imagePath = getImagePath(it.hotelInformation.thumbnailImages),
            distance = it.hotelAvailability.distance.toDouble(),
            formattedDistance = stringProvider.getString(
                R.string.search_results_item_distance_full,
                if (deviceLocaleProvider.isLanguageGerman()) it.hotelAvailability.distance * MILES_TO_KM
                else it.hotelAvailability.distance
            ),
            flag = it.hotelInformation.messagingFlag.toFlag(),
            pmsSource = it.hotelAvailability.pmsSource,
            cellCode = it.hotelAvailability.cellCode,
            parkingDrawableDescription = it.hotelInformation.parkingPair(),
            availability = it.hotelAvailability.toAvailability(),
            tripAdvisorRating = null,
            priceFrom = lowestRoomRate(it.hotelAvailability.lowestRoomRate, deviceLocaleProvider),
            fullyBookedText = stringProvider.getString(R.string.search_results_fully_booked),
            type = it.hotelAvailability.toAvailability().toLayoutType()
        )
    }
}

fun Availability.toLayoutType(): Int {
    return when (this) {
        OK, LIMITED -> R.layout.item_search_results_hotel_details
        SOLD_OUT -> R.layout.item_search_results_hotel_fully_booked
    }
}

class BookingCriteriaMapper @Inject constructor(private val stringProvider: StringResourceProvider) : Function<BookingCriteria, BookingCriteriaUi> {
    override fun apply(criteria: BookingCriteria): BookingCriteriaUi {

        val arrivalDateFormatted = criteria.arrivalDate.format(DateFormat.WEEKDAY_DAY_MONTH)
        val departureDateFormatted = criteria.departureDate.format(DateFormat.WEEKDAY_DAY_MONTH)

        return BookingCriteriaUi(searchItemName = criteria.searchCriteria.name,
                searchedLocation = criteria.searchCriteria.location!!,
                bookingCriteriaFormatted = stringProvider.getString(
                        R.string.search_results_toolbar, arrivalDateFormatted, departureDateFormatted,
                        stringProvider.getQuantityString(R.plurals.guests, criteria.numberOfGuests, criteria.numberOfGuests),
                        stringProvider.getQuantityString(R.plurals.rooms, criteria.numberOfRooms, criteria.numberOfRooms))
        )
    }
}

fun HotelListItem.toMapPinText(): String {
    return if (!fullyBooked) priceFrom else fullyBookedText.orEmpty()
}

@DrawableRes
fun HotelListItem.toMapDrawableRes(): Int {
    return when (availability) {
        OK, LIMITED -> {
            when (brand) {
                Hotel.Brand.HUB -> R.drawable.ic_map_pin_hotel_hub
                Hotel.Brand.ZIP -> R.drawable.ic_map_pin_hotel_zip
                else -> R.drawable.ic_map_pin_hotel_pi
            }
        }
        SOLD_OUT -> R.drawable.ic_map_pin_hotel_sold_out
    }
}

fun lowestRoomRate(lowestRoomRate: LowestRoomRateDomain?, deviceLocaleProvider: DeviceLocaleProvider): String {
    return lowestRoomRate?.let { lowerstRoomRate ->
        lowerstRoomRate.netTotal.toPriceDomain(lowestRoomRate.currencyCode)
            .formatted(deviceLocaleProvider)
    }?: EMPTY_STRING_DOMAIN
}

fun getImagePath(listOfThumbNails: List<ThumbnailImageDomain>?): String {
    return if (!listOfThumbNails.isNullOrEmpty()) {
        listOfThumbNails[0].imageSrc ?: EMPTY_STRING_DOMAIN
    } else EMPTY_STRING_DOMAIN
}

fun ShortHotelInformationDomain.parkingPair(): Pair<Int, String>? {
    if (this.hotelFacilities.getListOfParkingCodes().isNotEmpty()) {
        this.hotelFacilities.getListOfParkingCodes().forEach {
            return it.code.toFacility2Type().mapToPair(it)
        }
    }
    return null
}

fun List<FacilityItemDomain>.getListOfParkingCodes(): List<FacilityItemDomain> {
    val listOfParkingCodes = mutableListOf<FacilityItemDomain>()
    this.let { facilityList ->
        val parkingFacility = facilityList.filter { it.code == "CPF" || it.code == "COP" || it.code == "CPP" || it.code == "COC"}
        if (parkingFacility.isNotEmpty()) {
            parkingFacility.forEach { listOfParkingCodes.add(it) }
            return listOfParkingCodes
        } else {
            listOfParkingCodes
        }
    }
    return listOfParkingCodes
}

fun Facility2.Type.mapToPair(hotelFacilityDomain: FacilityItemDomain): Pair<Int, String> {
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
