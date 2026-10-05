package com.whitbread.premierinn.ciol.entity.upsells.details

import android.os.Parcelable
import com.whitbread.premierinn.ciol.entity.ReservationGuestUiModel
import com.whitbread.premierinn.ciol.mapper.convertToUiModelLeadBookerAddress
import com.whitbread.premierinn.ciol.mapper.toLeadBookerAddressDomainModel
import com.whitbread.premierinn.domain.booking.entity.BookingConfirmation
import com.whitbread.premierinn.domain.booking.entity.ReservationById
import com.whitbread.premierinn.domain.booking.entity.ReservationPackage
import com.whitbread.premierinn.domain.common.ReservationGuest
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookingConfirmationUiModel(
    val bookingReference: String,
    val balanceOutstanding: Float? = null,
    val bookingFlowID: String,
    val currencyCode: String,
    val hotelId: String,
    val infoMessages: List<String>,
    val newTotal: Float? = null,
    val policyCode: String,
    val previousTotal: Float? = null,
    val reservationByIdList: List<ReservationByIdUiModel>,
    val totalCost: Float,
    //val upgradeToFlex: UpgradeToFlex, IGNORED
    val basketStatus: String?
) : Parcelable

@Parcelize
data class ReservationByIdUiModel(
    val reservationId: String,
    val reservationStatus: String,
//    val billing: BillingResponse?, IGNORED
//    val depositPolicies: List<DepositPolicy>, IGNORED
    val reservationGuestList : List<ReservationGuestUiModel>,
    val reservationPackageList: List<ReservationPackageUiModel>,
    val roomStay: RoomStayUiModel,
    val purposeOfStay: String?
) : Parcelable

@Parcelize
data class ReservationPackageUiModel(
    val description: String,
    val packageCode: String,
    val unitPrice: Float,
    val totalQuantity: Int,
    val computedPrice: Float
) : Parcelable

fun BookingConfirmation.convertToBookingConfirmationUiModel() = BookingConfirmationUiModel(
    bookingReference = bookingReference,
    balanceOutstanding = balanceOutstanding,
    bookingFlowID = bookingFlowID,
    currencyCode = currencyCode,
    hotelId = hotelId,
    infoMessages = infoMessages,
    newTotal = newTotal,
    policyCode = policyCode,
    previousTotal = previousTotal,
    reservationByIdList = reservationByIdList.map { it.convertToReservationByIdUiModel() },
    totalCost = totalCost,
    basketStatus = basketStatus
)

fun ReservationById.convertToReservationByIdUiModel() = ReservationByIdUiModel(
    reservationId = reservationId,
    reservationStatus = reservationStatus,
    reservationGuestList = reservationGuestList.map { it.convertToReservationGuestUiModel() },
    reservationPackageList = reservationPackageList.map { it.convertToReservationPackageUiModel() },
    roomStay = roomStay.convertToRoomStayUiModel(),
    purposeOfStay = purposeOfStay
)

fun ReservationGuest.convertToReservationGuestUiModel() = ReservationGuestUiModel(
    profileId = profileId,
    reservationId = reservationId,
    firstName = firstName,
    lastName = lastName,
    title = title,
    email = email,
    dateOfBirth = dateOfBirth,
    passportNumber = passportNumber,
    nationality = nationality,
    isAccompanyingGuest = isAccompanyingGuest,
    address = address?.convertToUiModelLeadBookerAddress()
)

fun ReservationPackage.convertToReservationPackageUiModel() = ReservationPackageUiModel(
    description = description,
    packageCode = packageCode,
    unitPrice = unitPrice,
    totalQuantity = totalQuantity,
    computedPrice = computedPrice
)

fun BookingConfirmationUiModel.convertToBookingConfirmationDomain() = BookingConfirmation(
    bookingReference = bookingReference,
    balanceOutstanding = balanceOutstanding,
    bookingFlowID = bookingFlowID,
    currencyCode = currencyCode,
    hotelId = hotelId,
    infoMessages = infoMessages,
    newTotal = newTotal,
    policyCode = policyCode,
    previousTotal = previousTotal,
    reservationByIdList = reservationByIdList.map { it.convertToReservationById() },
    totalCost = totalCost,
    basketStatus = basketStatus
)

fun ReservationByIdUiModel.convertToReservationById() = ReservationById(
    reservationId = reservationId,
    reservationStatus = reservationStatus,
    reservationGuestList = reservationGuestList.map { it.convertToReservationGuest() },
    reservationPackageList = reservationPackageList.map { it.convertToReservationPackage() },
    roomStay = roomStay.convertToRoomStay(),
    purposeOfStay = purposeOfStay
)

fun ReservationGuestUiModel.convertToReservationGuest() = ReservationGuest(
    profileId = profileId,
    reservationId = reservationId,
    firstName = firstName,
    lastName = lastName,
    title = title,
    email = email,
    dateOfBirth = dateOfBirth,
    passportNumber = passportNumber,
    nationality = nationality,
    isAccompanyingGuest = isAccompanyingGuest,
    address = address?.toLeadBookerAddressDomainModel()
)

fun ReservationPackageUiModel.convertToReservationPackage() = ReservationPackage(
    description = description,
    packageCode = packageCode,
    unitPrice = unitPrice,
    totalQuantity = totalQuantity,
    computedPrice = computedPrice
)
