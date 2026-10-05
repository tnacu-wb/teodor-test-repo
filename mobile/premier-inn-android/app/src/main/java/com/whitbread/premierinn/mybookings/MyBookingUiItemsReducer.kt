package com.whitbread.premierinn.mybookings

import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.view.ListItem
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.entity.BookingHotelCountry
import com.whitbread.premierinn.domain.booking.entity.BookingStatus
import com.whitbread.premierinn.domain.booking.usecase.PAST_BOOKING_STATUS
import com.whitbread.premierinn.domain.common.QrKioskHotelsDomain
import io.reactivex.functions.BiFunction
import org.threeten.bp.LocalDate
import java.util.*

/**
 *
 */
class MyBookingUiItemsReducer(
    private val provider: StringResourceProvider,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val isCheckInOnlineFlagEnabled: Boolean,
    private val bookingStatus: List<BookingStatus>,
    private val bookingHotelCountry: List<BookingHotelCountry>,
    private val qrKioskHotelList: List<QrKioskHotelsDomain>
) :
    BiFunction<ArrayList<ListItem>, Booking, ArrayList<ListItem>> {
    override fun apply(uiModelList: ArrayList<ListItem>, booking: Booking): ArrayList<ListItem> {

        val canceledHeader =
            HeaderUiModel(provider.getString(R.string.my_bookings_cancelled_bookings))
        val activeHeader = HeaderUiModel(provider.getString(R.string.my_bookings_active_bookings))
        val pastHeader = HeaderUiModel(provider.getString(R.string.my_bookings_past_bookings))

        if (booking.isCancelled && !uiModelList.contains(canceledHeader)) {
            uiModelList.add(canceledHeader)
        } else if (!booking.isCancelled && !uiModelList.contains(pastHeader) && booking.departureDate.isBefore(LocalDate.now())) {
            uiModelList.add(pastHeader)
        } else if (!booking.isCancelled && !uiModelList.contains(activeHeader) && booking.bookingStatus != PAST_BOOKING_STATUS) {
            uiModelList.add(activeHeader)
        }
        uiModelList.add(
            MyBookingsMapper(
                deviceLocaleProvider.getDeviceLanguage(),
                isCheckInOnlineFlagEnabled,
                qrKioskHotelList
            ).apply(booking.updateStoredBookings(bookingStatus).updateStoredHotelCountryBookings(bookingHotelCountry))
        )

        return uiModelList
    }

    private fun Booking.updateStoredBookings(bookingStatusList: List<BookingStatus>): Booking {
        val matchingStatus = bookingStatusList.find { it.bookingReference == this.bookingReference }

        if (matchingStatus != null) {
            return this.copy(bookingStatus = matchingStatus.bookingStatus)
        }
        return this
    }

    private fun Booking.updateStoredHotelCountryBookings(hotelCountryList: List<BookingHotelCountry>): Booking {
        val matchingHotelCountryBooking = hotelCountryList.find { it.bookingReference == this.bookingReference }

        if (matchingHotelCountryBooking != null) {
            return this.copy(hotelCountry = matchingHotelCountryBooking.bookingHotelCountry)
        }
        return this
    }
}