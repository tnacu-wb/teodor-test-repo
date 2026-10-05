package com.whitbread.premierinn.bookingdetails

import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.mapper.convertToUiModelPreStayDetails
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.format.parkingPair
import com.whitbread.premierinn.common.format.withIcons
import com.whitbread.premierinn.common.utils.CallDestination
import com.whitbread.premierinn.common.utils.CallInfo
import com.whitbread.premierinn.data.graphql.mapper.toCommaSeparatedAddress
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.hoteldetails.entity.ContactDetailsDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain
import com.whitbread.premierinn.domain.common.mapToBrand
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.CUSTOMER_SERVICE_NUMBER
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.domain.search.entity.Location
import com.whitbread.premierinn.hoteldetails.uimodel.CallUsUiModel
import io.reactivex.functions.Function

class OperaBookingDetailsUiModelMapper(private val stringProvider: StringResourceProvider,
                                       private val getStringResource: GetStringResource) : Function<Pair<Booking, HotelInformationDomain>, BookingDetailsUiModel> {
    override fun apply(pair: Pair<Booking, HotelInformationDomain>): BookingDetailsUiModel {

        val (booking, hotel) = pair

        return BookingDetailsUiModel(
                name = hotel.name,
                address = hotel.address.toCommaSeparatedAddress(),
                location = Location(hotel.coordinates.latitude.toDouble(), hotel.coordinates.longitude.toDouble()),
                parkingPair = hotel.parkingPair(),
                parkingDescription = hotel.parkingDescription,
                facilities = hotel.hotelFacilities?.withIcons(),
                hotelBrand = mapToBrand(hotel.brand),
                rateName = booking.rateType,
                cancellable = booking.cancellable,
                cityTaxUrl = null ,
                showCityTaxInfo = false,
                businessUse = booking.details?.business ?: false,
                showPriceCityTaxLabel = booking.details?.cityTax != null,
                callUsUiModel = buildCallUiModelOpera(contactDetails = hotel.contactDetails,
                        stringResource = getStringResource,
                        stringProvider = stringProvider),
                accessibilityInfo = buildAccessibilityCallUiModelOpera(booking, hotel, getStringResource),
                preStayBooking = booking.preStayDetails?.convertToUiModelPreStayDetails(),
                isCheckInOnlineEnabled = booking.isCheckInOnlineAvailable,
                isCheckOutOnlineEnabled = booking.isCheckOutOnlineAvailable,
                basketStatus = booking.basketStatus
        )
    }

    fun buildCallUiModelOpera(
            order: Int = 0,
            contactDetails: ContactDetailsDomain?,
            stringResource: GetStringResource,
            stringProvider: StringResourceProvider
    ): CallUsUiModel {

        val callInfo = contactDetails.numberToCall(stringResource(CUSTOMER_SERVICE_NUMBER))

        fun hotelOrCustomerServiceLabel(): String {
            return when (callInfo.callDestination) {
                CallDestination.HOTEL -> stringProvider.getString(R.string.call_hotel)
                CallDestination.CALL_CENTRE -> stringProvider.getString(R.string.call_us)
            }
        }

        return CallUsUiModel.builder()
                .label(hotelOrCustomerServiceLabel())
                .telNumber(callInfo.number)
                .telCostInfo(
                        if (callInfo.isChargeable) stringResource(ContentManagedResourceRepository.Key.CHARGEABLE_PHONE_DESC)
                                .replace("\\n", "\n\n")
                        else stringResource(ContentManagedResourceRepository.Key.NON_CHARGEABLE_PHONE_DESC)
                )
                .order(order).build()
    }

    fun ContactDetailsDomain?.numberToCall(fallbackNumber: String): CallInfo {
        return this?.let {
                CallInfo(it.phone, false, CallDestination.HOTEL)
        } ?: CallInfo(fallbackNumber, false, CallDestination.CALL_CENTRE)
    }

    private fun buildAccessibilityCallUiModelOpera(booking: Booking, hotel: HotelInformationDomain, getStringResource: GetStringResource): AccessibilityUiModel? {
        val accessibleRoom = booking.rooms?.firstOrNull { it.roomType == RoomType.ACCESSIBLE }

        return accessibleRoom?.let{
            val callInfo = hotel.contactDetails.numberToCall(fallbackNumber = getStringResource(CUSTOMER_SERVICE_NUMBER))
            val (descriptionText, buttonText) = when (callInfo.callDestination) {
                CallDestination.CALL_CENTRE -> Pair(stringProvider.getString(R.string.booking_details_accessibility_body_callcentre), stringProvider.getString(R.string.call_us))
                CallDestination.HOTEL -> Pair(stringProvider.getString(R.string.booking_details_accessibility_body_hotel), stringProvider.getString(R.string.call_hotel))
            }
            AccessibilityUiModel(descriptionText, callInfo.number, buttonText)
        }
    }
}