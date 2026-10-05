package com.whitbread.premierinn.domain.booking.entity

import com.whitbread.premierinn.domain.common.ReservationGuest

data class BookingConfirmation(
    val bookingReference: String,
    val balanceOutstanding: Float? = null,
    val bookingFlowID: String,
    val currencyCode: String,
    val hotelId: String,
    val infoMessages: List<String>,
    val newTotal: Float? = null,
    val policyCode: String,
    val previousTotal: Float? = null,
    val reservationByIdList: List<ReservationById>,
    val totalCost: Float,
    val basketStatus: String?
)

data class ReservationById(
    val reservationId: String,
    val reservationStatus: String,
    val reservationGuestList : List<ReservationGuest>,
    val reservationPackageList: List<ReservationPackage>,
    val roomStay: RoomStay,
    val purposeOfStay: String?
)

data class ReservationPackage(
    val description: String,
    val packageCode: String,
    val unitPrice: Float,
    val totalQuantity: Int,
    val computedPrice: Float
)

data class RoomStay(
    val adultsNumber: Long,
    val childrenNumber: Long,
    val cot: Boolean,
    val roomType: String,
    val ratePlanCode: String,
    val arrivalDate: String,
    val departureDate: String,
    val roomPrice : Float,
)

fun BookingConfirmation?.hasChildren() =
    this?.reservationByIdList?.any { it.roomStay.childrenNumber.toInt() != 0 } == true

