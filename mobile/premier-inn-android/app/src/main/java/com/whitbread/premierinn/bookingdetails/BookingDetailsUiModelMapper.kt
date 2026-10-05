package com.whitbread.premierinn.bookingdetails

import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.mapper.convertToUiModelPreStayDetails
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.format.formatted
import com.whitbread.premierinn.common.format.parkingPair
import com.whitbread.premierinn.common.format.withIcons
import com.whitbread.premierinn.common.utils.CallDestination
import com.whitbread.premierinn.common.utils.buildCallUiModel
import com.whitbread.premierinn.common.utils.numberToCall
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.hotel.entity.CityTax
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.CUSTOMER_SERVICE_NUMBER
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import io.reactivex.functions.Function

class BookingDetailsUiModelMapper(private val stringProvider: StringResourceProvider,
                                  private val getStringResource: GetStringResource) : Function<Pair<Booking, Hotel>, BookingDetailsUiModel> {
    override fun apply(pair: Pair<Booking, Hotel>): BookingDetailsUiModel {

        val (booking, hotel) = pair

        return BookingDetailsUiModel(
                name = hotel.name,
                address = hotel.address.formatted(),
                location = hotel.location,
                parkingPair = hotel.parkingPair(),
                parkingDescription = null,
                facilities = hotel.facilities.withIcons(),
                hotelBrand = hotel.brand,
                rateName = booking.details?.rateName.orEmpty(),
                cancellable = booking.cancellable,
                cityTaxUrl = if (hotel.cityTax?.url.isNullOrEmpty()) stringProvider.getString(R.string.city_tax_web_url) else hotel.cityTax?.url!! ,
                showCityTaxInfo = isCityTaxInfoVisible(hotel.cityTax, booking.details?.business),
                businessUse = booking.details?.business ?: false,
                showPriceCityTaxLabel = booking.details?.cityTax != null,
                callUsUiModel = buildCallUiModel(contacts = hotel.contactDetails,
                        stringResource = getStringResource,
                        chargeable = false,
                        stringProvider = stringProvider),
                accessibilityInfo = buildAccessibilityCallUiModel(booking, hotel, getStringResource),
                preStayBooking = booking.preStayDetails?.convertToUiModelPreStayDetails(),
                isCheckInOnlineEnabled = booking.isCheckInOnlineAvailable,
                isCheckOutOnlineEnabled = booking.isCheckOutOnlineAvailable,
                basketStatus = booking.basketStatus
        )
    }

    private fun isCityTaxInfoVisible(cityTax: CityTax?, business: Boolean?): Boolean {
       return (cityTax?.isCityTaxHotel ?: false) && (cityTax?.isCityTaxBusinessHotel?.not() ?: false) && (business ?: false)
                || (cityTax?.isCityTaxHotel?.not() ?: false) && (cityTax?.isCityTaxBusinessHotel ?: false) && (business?.not() ?: false)
    }

    private fun buildAccessibilityCallUiModel(booking: Booking, hotel: Hotel, getStringResource: GetStringResource): AccessibilityUiModel? {
        val accessibleRoom = booking.rooms?.firstOrNull { it.roomType == RoomType.ACCESSIBLE }

        return accessibleRoom?.let{
            val callInfo = hotel.contactDetails.numberToCall(wantChargeable = false, fallbackNumber = getStringResource(CUSTOMER_SERVICE_NUMBER))
            val (descriptionText, buttonText) = when (callInfo.callDestination) {
                CallDestination.CALL_CENTRE -> Pair(stringProvider.getString(R.string.booking_details_accessibility_body_callcentre), stringProvider.getString(R.string.call_us))
                CallDestination.HOTEL -> Pair(stringProvider.getString(R.string.booking_details_accessibility_body_hotel), stringProvider.getString(R.string.call_hotel))
            }
            AccessibilityUiModel(descriptionText, callInfo.number, buttonText)
        }
    }
}