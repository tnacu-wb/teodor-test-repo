package com.whitbread.premierinn.data.roomupsell

import com.whitbread.premierinn.data.common.mapToPrice
import com.whitbread.premierinn.data.common.toLocalDate
import com.whitbread.premierinn.data.common.toPriceEntity
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationEntity
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomBreakdown
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.common.findExtraPackageWithCode
import com.whitbread.premierinn.domain.common.findThePackageWithCode
import com.whitbread.premierinn.domain.common.findThePackageWithCodeForKids
import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.PackagesSelectionDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomSelectionDomain
import com.whitbread.premierinn.domain.reservation.entity.Reservation

fun RoomUpsellEntity.toDomain(): Upsell {
    return Upsell(
            postingDate = this.postingDate,
            unitCost = this.unitCost.mapToPrice()!!,
            category = toUpsellCategoryDomain(this.category, this.code),
            code = this.code,
            roomId = this.roomId,
            legend = this.legend, // this is not need in domain
            quantity = this.quantity
    )
}

fun toUpsellCategoryDomain(category: String, code: String): Upsell.Category {
    return if (category == "BREAKFAST") {
        when (code) {
            "BFADBF" -> Upsell.Category.BREAKFAST
            "BFADCT" -> Upsell.Category.BREAKFAST
            "MDP" -> Upsell.Category.BREAKFAST
            "OBFBOX" -> Upsell.Category.BREAKFAST
            "BFCHDF" -> Upsell.Category.BREAKFAST
            "BBIB" -> Upsell.Category.BREAKFAST
            "BFGROL" -> Upsell.Category.BREAKFAST
            "BFGLGH" -> Upsell.Category.BREAKFAST
            "BFGCON" -> Upsell.Category.BREAKFAST
            "BFGCHD" -> Upsell.Category.BREAKFAST

            else -> Upsell.Category.OTHER
        }
    } else {
        when (category) {
            "F" -> Upsell.Category.BREAKFAST
            else -> Upsell.Category.OTHER
        }
    }
}

fun toReservationDomain(reference: String, originalReservation: AmendedReservationEntity,
                        listOfRooms: List<RoomCriteria>, listOfGuests: List<Guest>,
                        listOfUpsells: List<Upsell>, listOfRoomBreakdown: List<RoomBreakdown>): Reservation {
    return Reservation(
            bookingReference = reference,
            hotelCode = originalReservation.reservation.hotelCode,
            arrival = originalReservation.reservation.arrivalDate,
            departure = originalReservation.reservation.departureDate,
            roomsCriteria = listOfRooms,
            roomsLeadGuest = listOfGuests,
            upsells = listOfUpsells,
            cancelable = originalReservation.reservation.cancelable,
            roomsBreakdown = listOfRoomBreakdown
    )
}

fun RoomSelectionDomain.toRoomUpsellEntity(
    reservationReference: String,
    selectedPackage: PackagesSelectionDomain,
    packagesSelected: DataPackagesDomain,
    departureDate: String
): RoomUpsellEntity? {
    val findMealSelected = selectedPackage.findThePackageWithCode(packagesSelected)
    val findKidsMealSelected = selectedPackage.findThePackageWithCodeForKids(packagesSelected)
    val findExtrasSelected = selectedPackage.findExtraPackageWithCode(packagesSelected)

    if (findMealSelected != null) {
        return RoomUpsellEntity.forAmendedReservation(
            roomId = this.reservationId!!,
            amendedReservationReference = reservationReference,
            legend = findMealSelected.name,
            postingDate = departureDate.toLocalDate(),
            quantity = selectedPackage.noOfSelections,
            code = selectedPackage.id!!,
            category = "BREAKFAST",
            unitCost = PriceDomain(findMealSelected.price!!.toFloat(), findMealSelected.currency!!).toPriceEntity())
    } else if (findKidsMealSelected != null) {
        return RoomUpsellEntity.forAmendedReservation(
            roomId = this.reservationId!!,
            amendedReservationReference = reservationReference,
            legend = findKidsMealSelected.name,
            postingDate = departureDate.toLocalDate(),
            quantity = selectedPackage.noOfSelections,
            code = selectedPackage.id!!,
            category = "BREAKFAST",
            unitCost = PriceDomain(findKidsMealSelected.price!!.toFloat(), findKidsMealSelected.currency!!).toPriceEntity())
    } else if (findExtrasSelected != null) {
        return RoomUpsellEntity.forAmendedReservation(
            roomId = this.reservationId!!,
            amendedReservationReference = reservationReference,
            legend = findExtrasSelected.name ?: "Extra",
            postingDate = departureDate.toLocalDate(),
            quantity = selectedPackage.noOfSelections,
            code = selectedPackage.id!!,
            category = "OTHER",
            unitCost = PriceDomain(findExtrasSelected.price!!.toFloat(), findExtrasSelected.currency!!).toPriceEntity())
    } else return null
}

