package com.whitbread.premierinn.reviewbooking

import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.format.PriceFormat
import com.whitbread.premierinn.common.format.format
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.common.PriceDomain

data class ReviewBookModel(
    val reviewBookingInput: ReviewBookingInput,
    val cardFee: Float,
    val donation: PriceDomain,
    val totalPrice: PriceDomain,
    val deviceLocaleProvider: DeviceLocaleProvider,
    val donationPledgeString: String
) {

    var hotelImageUrl: String = EMPTY_STRING
    var hotelName: String = EMPTY_STRING
    var arrivalDateFormatted: String = EMPTY_STRING
    var departureDateFormatted: String = EMPTY_STRING
    var guests: Int = 1
    var rooms: Int = 1
    var allRoomsAreAccessible: Boolean = false
    var hotelStayCost: String = EMPTY_STRING
    var cardFeePrice: PriceDomain = PriceDomain.createDefault()

    val bookingFlowInput = reviewBookingInput.paymentDetailsInput()?.bookingFlowInput()
    val bookingPaymentProvider = reviewBookingInput.paymentDetailsInput()?.paymentProvider()

    init {
        if (bookingFlowInput != null) {
            hotelImageUrl = Urls.CONTENT_BASE_URL + bookingFlowInput.hotelImageReference()
            val departureDate = bookingFlowInput.arrivalDate().plusDays(bookingFlowInput.numNights().toLong())
            arrivalDateFormatted = bookingFlowInput.arrivalDate().format(DateFormat.SHORT_DATE_MONTH)
            departureDateFormatted = departureDate.format(DateFormat.SHORT_DATE_MONTH)
            guests = bookingFlowInput.numGuests()
            when {
                bookingFlowInput.roomBookings().isNotEmpty() -> {
                    rooms = bookingFlowInput.roomBookings().size +
                            (bookingFlowInput.accessibleRoomBookings()?.size ?:0) +
                            (bookingFlowInput.twinRoomBookings()?.size ?: 0)
                }

                !bookingFlowInput.accessibleRoomBookings().isNullOrEmpty() -> {
                    rooms = bookingFlowInput.accessibleRoomBookings()!!.size +
                            (bookingFlowInput.twinRoomBookings()?.size ?:0)
                    if (bookingFlowInput.twinRoomBookings().isNullOrEmpty()
                        && bookingFlowInput.roomBookings().isEmpty())
                        allRoomsAreAccessible = true
                }

                !bookingFlowInput.twinRoomBookings().isNullOrEmpty() -> {
                    rooms = bookingFlowInput.twinRoomBookings().size
                }
            }

            hotelStayCost = PriceFormat.format(bookingFlowInput
                .totalRoomsCost(reviewBookingInput.paymentDetailsInput()?.isTaxExempt
                    ?: false), deviceLocaleProvider)

            cardFeePrice = PriceDomain(cardFee, totalPrice.currency)
            hotelName = bookingFlowInput.hotelName()
        }
    }
}