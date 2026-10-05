package com.whitbread.premierinn.bookingdetails

import com.whitbread.premierinn.ciol.entity.PreStayDetailsUiModel
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import com.whitbread.premierinn.domain.search.entity.Location
import com.whitbread.premierinn.hoteldetails.uimodel.CallUsUiModel


data class BookingDetailsState(val type: StateType,
                               val bookingUiModel: BookingBasicsUiModel? = null,
                               val bookingDetails: BookingDetailsUiModel? = null) {
    enum class StateType {
        Idle, InFlight, Success, Error
    }

    companion object {
        @JvmStatic
        fun idle(): BookingDetailsState {
            return BookingDetailsState(StateType.Idle)
        }

        @JvmStatic
        fun inFlight(bookingBasics: BookingBasicsUiModel?): BookingDetailsState {
            return BookingDetailsState(type = StateType.InFlight, bookingUiModel = bookingBasics)
        }

        @JvmStatic
        fun error(): BookingDetailsState {
            return BookingDetailsState(StateType.Error)
        }

        @JvmStatic
        fun success(bookingBasics: BookingBasicsUiModel?, bookingDetails: BookingDetailsUiModel? = null): BookingDetailsState {
            return BookingDetailsState(StateType.Success, bookingUiModel = bookingBasics, bookingDetails = bookingDetails)
        }
    }
}

data class BookingBasicsUiModel(val booking: Booking,
                                val numberOfNightsFormatted: String,
                                val numberOfRoomsFormatted: String,
                                val checkInDateFormatted: String,
                                val checkOutDateFormatted: String,
                                val totalCostFormatted: String,
                                val bannerMessage: BannerMessage?,
                                val justBooked: Boolean,
                                val infoMessage: String?,
                                val isCheckInOnlineEnabled: Boolean,
                                val isCheckInOnlineFlagEnabled: Boolean,
                                val isCheckOutOnlineEnabled: Boolean,
                                val basketStatus: String?,
                                val isPrepaid: Boolean) {
    data class BannerMessage(val type: Type, val text: String) {
        enum class Type {
            CANCELLED,
            SUCCESS,
            INFORMATION
        }
    }
}

data class BookingDetailsUiModel(val name: String,
                                 val rateName: String,
                                 val cancellable: Boolean?,
                                 val location: Location,
                                 val address: String,
                                 val parkingPair: Pair<Int, String>?,
                                 val parkingDescription: String?,
                                 val hotelBrand: Hotel.Brand,
                                 val cityTaxUrl: String?,
                                 val showCityTaxInfo: Boolean,
                                 val businessUse: Boolean,
                                 val showPriceCityTaxLabel: Boolean,
                                 val facilities: List<Pair<Int, String>>?,
                                 val accessibilityInfo: AccessibilityUiModel?,
                                 val callUsUiModel: CallUsUiModel?,
                                 val preStayBooking: PreStayDetailsUiModel?,
                                 val isCheckInOnlineEnabled: Boolean,
                                 val isCheckOutOnlineEnabled: Boolean,
                                 val basketStatus: String?
)

data class AccessibilityUiModel(val descriptionText: String,
                                val phoneNumber: String,
                                val callButtonText: String)

