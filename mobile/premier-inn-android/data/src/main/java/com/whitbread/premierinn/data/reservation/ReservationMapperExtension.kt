package com.whitbread.premierinn.data.reservation

import com.whitbread.premierinn.data.common.mapToPrice
import com.whitbread.premierinn.data.common.throwApiExceptionIfNullable
import com.whitbread.premierinn.data.common.toApiAddress
import com.whitbread.premierinn.data.remote.AmendReservationRequest
import com.whitbread.premierinn.data.remote.ApiCommon
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationAndAmendSummaryGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationGraphQLContract
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationEntity
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationWithDetails
import com.whitbread.premierinn.data.roombreakdown.RoomBreakdownEntity
import com.whitbread.premierinn.data.roombreakdown.toDomain
import com.whitbread.premierinn.data.roomcriteria.RoomCriteriaEntity
import com.whitbread.premierinn.data.roomcriteria.toDomain
import com.whitbread.premierinn.data.roomguest.RoomGuestEntity
import com.whitbread.premierinn.data.roomguest.toDomain
import com.whitbread.premierinn.data.roomupsell.RoomUpsellEntity
import com.whitbread.premierinn.data.roomupsell.toDomain
import com.whitbread.premierinn.domain.common.Breakfast
import com.whitbread.premierinn.domain.common.FREE_CHILD_BREAKFAST_CODE
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.HUB_BREAKFAST_CODE
import com.whitbread.premierinn.domain.common.PaymentDetails
import com.whitbread.premierinn.domain.common.RoomBreakdown
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.common.UpsellAvailable
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import org.threeten.bp.LocalDate

const val SINGLE_KEY = "SB"
const val DOUBLE_KEY = "DB"
const val TWIN_KEY = "TWIN"
const val FAMILY_KEY = "FAM"
const val ACCESSIBLE_KEY = "DIS"

fun BookingConfirmationGraphQLContract.BookingConfirmationData.toAmendedReservationEntity(
    tempBasketRef: String, originalBookingReference: String): AmendedReservationEntity {
    return AmendedReservationEntity.create(
        basketReference = tempBasketRef,
        bookingReference = originalBookingReference,
        arrivalDate = LocalDate.parse(this.data.bookingConfirmation!!.reservationByIdList[0].roomStay.arrivalDate),
        departureDate = LocalDate.parse(this.data.bookingConfirmation.reservationByIdList[0].roomStay.departureDate),
        hotelCode = this.data.bookingConfirmation.hotelId,
        cancelable = this.data.manageBooking?.isCancellable ?: false)
}

fun BookingConfirmationAndAmendSummaryGraphQLContract.BookingConfirmationAndAmendSummaryData.toAmendedReservationEntity(
    tempBasketRef: String, originalBookingReference: String): AmendedReservationEntity {
    return AmendedReservationEntity.create(
        basketReference = tempBasketRef,
        bookingReference = originalBookingReference,
        arrivalDate = LocalDate.parse(this.data.bookingConfirmation!!.reservationByIdList[0].roomStay.arrivalDate),
        departureDate = LocalDate.parse(this.data.bookingConfirmation.reservationByIdList[0].roomStay.departureDate),
        hotelCode = this.data.bookingConfirmation.hotelId,
        cancelable = this.data.manageBooking?.isCancellable ?: false)
}

fun AmendedReservationWithDetails.toDomain(): Reservation {
    val amendedReservationEntity = this.reservationEntity.reservation

    return Reservation(
            bookingReference = amendedReservationEntity.bookingReference,
            hotelCode = amendedReservationEntity.hotelCode,
            arrival = amendedReservationEntity.arrivalDate,
            departure = amendedReservationEntity.departureDate,
            roomsCriteria = this.roomCriteria.asIterable().map { it.toDomain() }.toList(),
            roomsLeadGuest = this.roomGuests.asIterable().map { it.toDomain() }.toList(),
            roomsBreakdown = this.roomBreakdown.asIterable().map { it.toDomain() }.toList(),
            upsells = this.roomUpsells.asIterable().map { it.toDomain() }.toList(),
            cancelable = amendedReservationEntity.cancelable
    )
}

fun AmendedReservationWithDetails.sortRooms(): AmendedReservationWithDetails {
    return AmendedReservationWithDetails(
            reservationEntity = reservationEntity,
            roomCriteria = roomCriteria.sortedBy { it.id },
            roomGuests = roomGuests,
            roomUpsells = roomUpsells,
            roomBreakdown = roomBreakdown
    )
}

fun Reservation.toEntity(basketReference: String): AmendedReservationEntity {
    return AmendedReservationEntity.create(
            basketReference = basketReference,
            bookingReference = throwApiExceptionIfNullable(bookingReference),
            arrivalDate = throwApiExceptionIfNullable(arrival),
            departureDate = throwApiExceptionIfNullable(departure),
            hotelCode = throwApiExceptionIfNullable(hotelCode),
            cancelable = throwApiExceptionIfNullable(cancelable))
}

fun RoomCriteria.toEntity(reservationReference: String, roomId: String): RoomCriteriaEntity {
    return RoomCriteriaEntity(
            roomId = roomId,
            amendedReservationReference = reservationReference,
            roomType = roomType,
            numberOfAdults = numberOfAdults,
            numberOfChildren = numberOfChildren,
            cot = includeCot)
}

fun Guest.toEntity(reservationReference: String, roomId: String): RoomGuestEntity {
    return RoomGuestEntity(
            title = title,
            firstName = firstName,
            lastName = lastName,
            guestHistoryNumber = guestHistoryNumber,
            emailAddress = emailAddress,
            amendedReservationReference = reservationReference,
            roomId = roomId)
}

fun Upsell.toEntity(reservationReference: String, roomId: String): RoomUpsellEntity {
    return RoomUpsellEntity(
            roomId = roomId,
            legend = legend,
            category = category.name,
            code = code,
            quantity = quantity,
            postingDate = postingDate,
            unitCost = throwApiExceptionIfNullable(unitCost.mapToPrice()),
            amendedReservationReference = reservationReference
    )
}

fun RoomBreakdown.toEntity(reservationReference: String, roomId: String): RoomBreakdownEntity {
    return RoomBreakdownEntity(
            roomId = roomId,
            unitCost = throwApiExceptionIfNullable(totalRoomCost.mapToPrice()),
            amendedReservationReference = reservationReference
    )
}

fun List<RoomCriteria>.toRequestRoomGuests(): List<AmendReservationRequest.RoomRequests> {
    return this.asSequence().asIterable().map {
        AmendReservationRequest.RoomRequests(
                roomId = it.roomId,
                roomType = it.roomType.toApiRoomString(),
                adults = it.numberOfAdults,
                cot = it.includeCot,
                children = it.numberOfChildren)
    }.toList()
}

fun List<Guest>.toRequestGuest(language: String): List<AmendReservationRequest.Guest> {
    return this.asSequence().asIterable().mapIndexed { index: Int, it: Guest ->
        AmendReservationRequest.Guest(
                title = it.title,
                firstName = it.firstName,
                lastName = it.lastName,
                guestHistoryNumber = it.guestHistoryNumber,
                roomNumber = index + 1)
    }.toList()
}

fun List<Upsell>.toRequestUpsells(): List<AmendReservationRequest.UpsellItem> {
    val roomIds = this.map { it.roomId }.distinct()
    // Filter out Breakfast Upsells
    return this.filter {
        it.category != Upsell.Category.BREAKFAST
                || it.code != HUB_BREAKFAST_CODE
    }.asIterable().mapIndexed { _: Int, it: Upsell ->
        AmendReservationRequest.UpsellItem(
                postingDate = it.postingDate,
                quantity = it.quantity,
                code = it.code,
                legend = it.legend,
                roomId = it.roomId,
                roomNumber = (roomIds.indexOf(it.roomId) + 1).toString(),
                category = it.category.name,
                unitCost = ApiCommon.Price(it.unitCost.amount, it.unitCost.currency),
                subTotal = ApiCommon.Price(it.unitCost.amount.times(it.quantity), it.unitCost.currency))
    }.toList()
}

fun List<Breakfast>.toRequestBreakfast(): List<AmendReservationRequest.BreakfastRequest> {
    return this.asIterable().mapIndexed { _: Int, it: Breakfast ->
        AmendReservationRequest.BreakfastRequest(
                adults = it.adults,
                children = it.children,
                code = it.code,
                roomNumber = it.roomNumber)
    }.toList()
}

fun List<Upsell>.toBreakfast(criteria: List<RoomCriteria>, availableUpsells: List<UpsellAvailable>): List<Breakfast> {
    // Breakfast is filtered from the Upsells
    val groupedUpSells = this.filter {
        it.category == Upsell.Category.BREAKFAST
                || it.code == HUB_BREAKFAST_CODE
    }
            .groupBy { Pair(it.roomId, it.code) }
            .map { it.value.first() }
    val roomIds = this.map { it.roomId }.distinct()
    val breakfasts = mutableListOf<Breakfast>()

    criteria.forEach { criteriaItem ->
        groupedUpSells.forEach { upsell ->
            if (criteriaItem.roomId == upsell.roomId) {

                val availableUpsellItem = availableUpsells.find { it.code == upsell.code }

                // Setting default values
                var numberOfAdults = if (upsell.code == FREE_CHILD_BREAKFAST_CODE) 0 else criteriaItem.numberOfAdults

                var numberOfChildren = criteriaItem.numberOfChildren

                // Validating [numberOfChildren] based on upsellItemAvialable criteria
                availableUpsellItem?.let {
                    if (it.freeBreakfastTrigger && it.freeBreakfastCode == FREE_CHILD_BREAKFAST_CODE)
                        numberOfChildren = 0
                }

                breakfasts.add(Breakfast(
                        adults = numberOfAdults,
                        children = numberOfChildren,
                        code = upsell.code,
                        roomNumber = (roomIds.indexOf(upsell.roomId) + 1)
                ))
            }
        }
    }

    return breakfasts
}

fun PaymentDetails.toPaymentCard(): AmendReservationRequest.PaymentCard {
    return AmendReservationRequest.PaymentCard(
            billingAddress = billingAddress?.toApiAddress(),
            cardType = cardType,
            cardNumber = cardNumber,
            cardSecurityCode = cardSecurityCode,
            holdersFullName = holdersFullName,
            expiryDate = expiryDate,
            issueNumber = issueNumber,
            startDate = startDate,
            prepaymentRequired = prepaymentRequired,
            useExistingCard = useExistingCard
    )
}

fun Reservation.toAmendRequest(upsellsAvailable: List<UpsellAvailable>,
                               paymentDetails: PaymentDetails,
                               cardSecurityCode: String,
                               language: String): AmendReservationRequest.ReservationRequest {
    return AmendReservationRequest.ReservationRequest(
            this.arrival,
            this.departure,
            this.roomsCriteria.size,
            this.roomsCriteria.toRequestRoomGuests(),
            this.roomsLeadGuest.toRequestGuest(language),
            this.upsells.toRequestUpsells(),
            this.upsells.toBreakfast(roomsCriteria, upsellsAvailable).toRequestBreakfast(),
            paymentDetails.copy(useExistingCard = true, cardSecurityCode = cardSecurityCode).toPaymentCard())
}

fun RoomType.toApiRoomString(): String? {
    return when (this) {
        RoomType.SINGLE -> SINGLE_KEY
        RoomType.DOUBLE -> DOUBLE_KEY
        RoomType.TWIN -> TWIN_KEY
        RoomType.FAMILY -> FAMILY_KEY
        RoomType.ACCESSIBLE -> ACCESSIBLE_KEY
        else -> null
    }
}