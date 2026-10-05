package com.whitbread.premierinn.amend.amendreview

import com.whitbread.premierinn.amend.AmendStringProvider
import com.whitbread.premierinn.amend.amendreview.uimodel.AmendedTotal
import com.whitbread.premierinn.amend.amendreview.uimodel.BookingPaymentInfo
import com.whitbread.premierinn.amend.amendreview.uimodel.BookingSummary
import com.whitbread.premierinn.amend.calculateUpsellTotalCost
import com.whitbread.premierinn.amend.priceDifference
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.format.PriceFormat
import com.whitbread.premierinn.common.utils.sumByFloat
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.DONATION_LIST
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.common.nights
import com.whitbread.premierinn.domain.reservation.entity.Reservation

data class ReviewAmendState(
    private val amendedReservation: AsyncResult<Reservation>? = null,
    private val originalReservation: Reservation? = null,
    val originalBooking: AsyncResult<Booking>? = null,
    private val stringResourceProvider: AmendStringProvider,
    private val dateChanged: ReviewAmendViewModel.ReviewAmendState.DateChanged? = null,
    private val mealsChanged: ReviewAmendViewModel.ReviewAmendState.MealsChanged? = null,
    private val roomChanged: ReviewAmendViewModel.ReviewAmendState.RoomChanged? = null,
    private val roomAdded: ReviewAmendViewModel.ReviewAmendState.RoomAdded? = null,
    private val roomRemoved: ReviewAmendViewModel.ReviewAmendState.RoomRemoved? = null,
    private val extrasChanged: ReviewAmendViewModel.ReviewAmendState.ExtrasChanged? = null,
    val billingAddress: String? = null,
    private val bookingSummaryCreated: ReviewAmendViewModel.ReviewAmendState.BookingSummaryCreated? = null,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    val confirmPollingMessage: String? = null) {
    val isLoading: Boolean
        get() = amendedReservation is AsyncResult.Loading || originalBooking is AsyncResult.Loading

    val getAmendedReservation: Reservation?
        get() = if (amendedReservation is AsyncResult.Success && amendedReservation.data != null) {
            amendedReservation.data
        } else null

    val getOriginalReservation: Reservation?
        get() = originalReservation

    val bookingPaymentInfo: BookingPaymentInfo?
        get() = if (originalBooking is AsyncResult.Success && originalBooking.data != null) {
            prepaid.let { prepaid ->
                when (prepaid) {
                    true -> BookingPaymentInfo(stringResourceProvider.totalPaidText, PriceFormat.format(originalBooking.data.prepaidAmount!!.amount,
                        originalBooking.data.prepaidAmount!!.currency, deviceLocaleProvider))
                    else -> BookingPaymentInfo(stringResourceProvider.previousPaidText, PriceFormat.format(originalBooking.data.totalCost!!.amount,
                        originalBooking.data.totalCost!!.currency, deviceLocaleProvider))
                }
            }
        } else null

    private val outStandingAmount: PriceDomain?
        get() = if (originalBooking is AsyncResult.Success && originalBooking.data != null) {
            originalBooking.data.totalCost?.let { originalTotalCost ->
                originalBooking.data.prepaidAmount?.let { prepaidAmount ->
                    if (originalTotalCost.amount > prepaidAmount.amount) {
                        PriceDomain(Math.round((originalTotalCost.amount - prepaidAmount.amount) * 100.0f) / 100.0f, originalTotalCost.currency)
                    } else null
                }
            }
        } else null

    val prepaid: Boolean?
        get() = if (originalBooking is AsyncResult.Success && originalBooking.data != null) {
            originalBooking.data.prepaidAmount != null
        } else null

    private val hasOutStandingAmount: Boolean = outStandingAmount != null

    val getAmendedNumOfRooms: Int
    get() = if (originalBooking is AsyncResult.Success && originalBooking.data != null) {
        originalBooking.data.numberOfRooms
    } else 0

            val getBalance: AmendedTotal?
        get() = if (originalBooking is AsyncResult.Success && originalBooking.data != null && getAmendedTotal != null) {
            when {
                hasOutStandingAmount -> {
                    AmendedTotal(title = stringResourceProvider.balanceOutstandingTitle,
                            description = stringResourceProvider.balanceOutstandingTextPayOnArrival,
                            amount = PriceFormat.format(outStandingAmount!!.amount + getAmendedTotal!!.amount,
                                outStandingAmount!!.currency, deviceLocaleProvider))
                }
                originalBooking.data.prepaid && getAmendedTotal!!.amount >= 0.0f -> {
                    AmendedTotal(title = stringResourceProvider.balanceOutstandingTitle,
                            description = stringResourceProvider.balanceOutstandingTextPayOnArrival,
                            amount = PriceFormat.format(getAmendedTotal!!.amount,
                                getAmendedTotal!!.currency, deviceLocaleProvider))
                }
                !originalBooking.data.prepaid -> {
                    AmendedTotal(title = stringResourceProvider.balanceOutstandingTitle,
                            description = stringResourceProvider.balanceOutstandingTextPayOnArrival,
                            amount = PriceFormat.format(originalBooking.data.totalCost!!.amount + getAmendedTotal!!.amount,
                                getAmendedTotal!!.currency, deviceLocaleProvider))
                }
                else -> {
                    val formatNegativePrice = getAmendedTotal!!.amount * -1
                    AmendedTotal(title = stringResourceProvider.refundTitle,
                            description = stringResourceProvider.refundDescription,
                            amount = PriceFormat.format(formatNegativePrice,
                                getAmendedTotal!!.currency, deviceLocaleProvider))
                }
            }
        } else null

    val submitBookingSummary: BookingSummary?
        get() = if (getAmendedReservation != null && getAmendedTotal != null && originalBooking is AsyncResult.Success && originalBooking.data != null && originalReservation != null) {
            val donation = originalReservation.upsells.find { it.code in DONATION_LIST }
            getAmendedReservation?.let { reservation ->
                originalBooking.data.let { booking ->
                    val amendedDates = reservation.arrival to reservation.departure
                    BookingSummary(reservation.roomsCriteria,
                            donation?.let { upsell ->
                                reservation.upsells.plus(booking.upsells.filter { it.category == Upsell.Category.OTHER }).plus(upsell)
                            } ?: reservation.upsells.plus(booking.upsells.filter { it.category == Upsell.Category.OTHER }),
                            amendedDates, getAmendedTotal!!,
                            booking.totalCost!!)
                }
            }
        } else null

    val submitList: Boolean?
        get() = if (getAmendedReservation != null || bookingPaymentInfo != null || getAmendedTotal != null) {
            true
        } else null

    val onDateChanged: ReviewAmendViewModel.ReviewAmendState.DateChanged?
        get() = dateChanged

    val onMealsChanged: ReviewAmendViewModel.ReviewAmendState.MealsChanged?
        get() = mealsChanged

    val onRoomChanged: ReviewAmendViewModel.ReviewAmendState.RoomChanged?
        get() = roomChanged

    val onRoomAdded: ReviewAmendViewModel.ReviewAmendState.RoomAdded?
        get() = roomAdded

    val onRoomRemoved: ReviewAmendViewModel.ReviewAmendState.RoomRemoved?
        get() = roomRemoved

    val onExtrasChanged: ReviewAmendViewModel.ReviewAmendState.ExtrasChanged?
        get() = extrasChanged

    val onBookingSummaryCreated: ReviewAmendViewModel.ReviewAmendState.BookingSummaryCreated?
        get() = bookingSummaryCreated

    val getAmendedTotal: PriceDomain?
        get() = if (originalBookingTotalCost != null && amendedTotalCost != null) {
            priceDifference(amendedTotalCost!!, originalBookingTotalCost!!)
        } else null

    private val amendedTotalCost: Float?
        get() = if (amendedReservation is AsyncResult.Success && amendedReservation.data != null) {
            amendedReservation.data.roomsBreakdown.sumByFloat { it.totalRoomCost.amount } +
                    calculateUpsellTotalCost(amendedReservation.data.upsells) * amendedReservation.data.nights()
        } else null

    private val originalBookingTotalCost: PriceDomain?
        get() = if (originalBooking is AsyncResult.Success && originalBooking.data != null) {
            originalBooking.data.totalCost
        } else null
}