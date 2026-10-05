package com.whitbread.premierinn.mybookings


import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.format.format
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.QrKioskHotelsDomain
import com.whitbread.premierinn.domain.common.translateFullNameToEnglishIfApplicable
import io.reactivex.functions.Function

class MyBookingsMapper(
    private val language: String,
    private val isCheckInOnlineFlagEnabled: Boolean,
    private val qrKioskHotelList: List<QrKioskHotelsDomain>
) : Function<Booking, BookingUiModel> {
    override fun apply(it: Booking): BookingUiModel {
        return BookingUiModel(
            id = it.bookingReference,
            leadGuestSurname = it.leadGuestSurname,
            leadGuestFullName = it.leadGuestFullName.translateFullNameToEnglishIfApplicable(language),
            hotelCode = it.hotelCode,
            hotelName = it.hotelName,
            dates = it.arrivalDate.format(DateFormat.WEEKDAY_DAY_MONTH)
                .plus(" - ")
                .plus(it.departureDate.format(DateFormat.WEEKDAY_DAY_MONTH)),
            arrivalDate = it.arrivalDate,
            departureDate = it.departureDate,
            isPrepaid = it.prepaid,
            isBusinessBooking = it.isBusinessBooking,
            bookingStatus = it.bookingStatus,
            isCheckInOnlineAvailable = it.isCheckInOnlineAvailable,
            isCheckInOnlineFlagEnabled = isCheckInOnlineFlagEnabled,
            basketStatus = it.basketStatus,
            qrKioskHotels = qrKioskHotelList,
            isCancelled = it.isCancelled,
            hotelCountry = it.hotelCountry,
            isThirdPartyBooking = it.isThirdPartyBooking
        )
    }
}
