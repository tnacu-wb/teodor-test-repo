package com.whitbread.premierinn.summary

import android.view.View
import com.whitbread.premierinn.api.response.AcceptedCreditCard
import com.whitbread.premierinn.api.response.availability.UpsellItem
import com.whitbread.premierinn.bookingdetails.UpsellItemSummary.UpsellItemType
import com.whitbread.premierinn.businessbooker.domain.company.CompanyManagementDetails
import com.whitbread.premierinn.businessbooker.domain.company.ManagementInformationQuestion
import com.whitbread.premierinn.common.RoomBooking
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.summary.model.SummaryRoomItem
import com.whitbread.premierinn.domain.common.EMPLOYEE_CODE
import com.whitbread.premierinn.domain.common.EMPLOYEE_RATE_PLAN_CODE
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.OPERA_FREE_CHILD_BREAKFAST
import com.whitbread.premierinn.domain.common.QUESTION_BOOKING_FLOW
import com.whitbread.premierinn.domain.common.isAlternativeRoomOpera
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomRateDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PreviousRoomsSelections
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ReservationPackage
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Reservations
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomRates
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomsSelections
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveReservationWithAncillariesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SelectedPackages
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethodsDetailsInput
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

fun List<RoomBooking>.toListOfReservations(
    operaAccessibleRooms: List<RoomBooking>?,
    operaTwinRooms: List<RoomBooking>?,
    hotelId: String,
    arrival: String,
    departure: String,
    rateType: String,
    specialRequests: List<List<String>>?,
    packageCode: List<String>?,
    packageAmount: List<Double?>?,
    promotionCode: String?,
    promoKind: String?
): List<Reservations> {
    val listOfReservations = mutableListOf<Reservations>()
    val addMultipleRoomBookingListAndOrderIt = listOfNotNull(this, operaAccessibleRooms, operaTwinRooms).flatten()
        .sortedBy { it.roomNumber }

    for ((index, room) in addMultipleRoomBookingListAndOrderIt.withIndex()) {
        val specialRequestList = if (specialRequests != null && index < specialRequests.size) specialRequests[index] else null
        val reservationPackage = mutableListOf<ReservationPackage>()

        if (packageCode != null && packageAmount != null) {
            if (packageCode.isNotEmpty() && packageAmount.isNotEmpty()
                && index in packageAmount.indices && index in packageCode.indices
                && packageCode[index] != EMPTY_STRING_DOMAIN && packageAmount[index] != null
            ) {
                reservationPackage.add(
                    ReservationPackage(
                        packageAmount[index]!!.toFloat(),
                        1,
                        packageCode[index],
                        arrival,
                        departure
                    )
                )
            }
        }

        listOfReservations.add(Reservations(
            hotelId = hotelId,
            arrival = arrival,
            departure = departure,
            adultsNumber = room.adults,
            childrenNumber = room.children,
            basketReferenceId = EMPTY_STRING_DOMAIN,
            cotRequired = false,
            roomRates = RoomRates(
                arrival,
                departure,
                room.lettingCode,
                specialRequestList,
                rateType,
                promotionCode,
                if (!promoKind.isNullOrEmpty()) promoKind else null
            ),
            reservationPackages = reservationPackage.takeUnless { it.isNullOrEmpty() }
        ))
    }

    return listOfReservations
}

fun List<RoomBooking>.addTwoRoomBookingListAndOrderIt(anotherList: List<RoomBooking>): List<RoomBooking> {
   return this.plus(anotherList).sortedBy { it.roomNumber }
}

fun List<RoomBooking>.addThreeRoomBookingListAndOrderIt(secondList: List<RoomBooking>, thirdList: List<RoomBooking>): List<RoomBooking> {
    return this.plus(secondList).plus(thirdList).sortedBy { it.roomNumber }
}

fun HotelAvailabilityDomain.getSpecialRequestsList(rateType: String, alternativeRoomChosen: Boolean):  List<List<String>>  {
    val listOfSpecialRequestRoom = mutableListOf<List<String>>()

    val selectedRate = filterSelectedRatesRoomRateList(rateType)
    selectedRate!!.first().roomTypesDomainList.forEach { eachRoom ->
        if (alternativeRoomChosen) {
            val getAlternativeRoom =
                eachRoom.roomOptionsDomainList.filter { it.roomClass.isAlternativeRoomOpera() }
            getAlternativeRoom.firstOrNull()?.specialRequests?.let { specialRequests ->
                listOfSpecialRequestRoom.add(specialRequests)
            }
        } else {
            val getStdRoom =
                eachRoom.roomOptionsDomainList.filter { !it.roomClass.isAlternativeRoomOpera() }
            listOfSpecialRequestRoom.add(getStdRoom.first().specialRequests!!)
        }
    }

    return listOfSpecialRequestRoom
}

fun HotelAvailabilityDomain.getPackageCode(rateType: String, alternativeRoomChosen: Boolean): List<String?> {
    val packageCode = mutableListOf<String?>()

    val selectedRate = filterSelectedRatesRoomRateList(rateType)
    selectedRate!!.first().roomTypesDomainList.forEach { eachRoom ->
        if (alternativeRoomChosen) {
            val getAlternativeRoom =
                eachRoom.roomOptionsDomainList.filter { it.roomClass.isAlternativeRoomOpera() }
            getAlternativeRoom.firstOrNull()?.roomPriceBreakdownDomain?.packageCode?.let {
                packageCode.add(it)
            }
        } else {
            val getStdRoom =
                eachRoom.roomOptionsDomainList.filter { !it.roomClass.isAlternativeRoomOpera() }
            packageCode.add(getStdRoom.first().roomPriceBreakdownDomain.packageCode)
        }
    }

    return packageCode
}

fun HotelAvailabilityDomain.getPackageAmount(rateType: String, alternativeRoomChosen: Boolean): List<Double?> {
    var packageAmount = mutableListOf<Double?>()

    val selectedRate = filterSelectedRatesRoomRateList(rateType)
    selectedRate!!.first().roomTypesDomainList.forEach { eachRoom ->
        if (alternativeRoomChosen) {
            val getAlternativeRoom =
                eachRoom.roomOptionsDomainList.filter { it.roomClass.isAlternativeRoomOpera() }
            getAlternativeRoom.firstOrNull()?.roomPriceBreakdownDomain?.packageAmount?.let {
                packageAmount.add(getAlternativeRoom.first().roomPriceBreakdownDomain.packageAmount)
            }
        } else {
            val getStdRoom =
                eachRoom.roomOptionsDomainList.filter { !it.roomClass.isAlternativeRoomOpera() }
            packageAmount.add(getStdRoom.first().roomPriceBreakdownDomain.packageAmount)
        }
    }

    return packageAmount
}

private fun HotelAvailabilityDomain.filterSelectedRatesRoomRateList(
    rateType: String
): List<RoomRateDomain>? {
    val selectedRate = if (rateType == EMPLOYEE_RATE_PLAN_CODE) {
        this.roomRateDomainList?.filter { it.cellCode == EMPLOYEE_CODE }
    } else {
        this.roomRateDomainList?.filter { it.ratePlanCode == rateType }
    }
    return selectedRate
}

fun SummaryInput.totalRooms(): Int {
    return (this.roomBookings()?.size ?: 0) + (this.twinRoomBookings()?.size ?: 0) + (this.accessibleRoomBookings()?.size ?: 0)
}

fun getOperaBreakFastCode(breakFastType: String): String = when (breakFastType) {
    UpsellItemType.PI_BREAKFAST.code() -> UpsellItemType.OPERA_PI_BREAKFAST.code()
    UpsellItemType.CONTINENTAL_BREAKFAST.code() -> UpsellItemType.OPERA_CONTINENTAL_BREAKFAST.code()
    UpsellItemType.MEAL_DEAL.code() -> UpsellItemType.OPERA_MEAL_DEAL.code()
    UpsellItemType.BREAKFAST_BOX.code() -> UpsellItemType.OPERA_BREAKFAST_BOX.code()
    UpsellItemType.FREE_CHILD_BREAKFAST.code() -> UpsellItemType.OPERA_FREE_CHILD_BREAKFAST.code()
    else -> UpsellItemType.NO_PREFERENCE.code()
}

fun String.toShortDateFormat(): String =
    LocalDate.parse(this, DateTimeFormatter.ofPattern(DateFormat.DASHED_YEAR_MONTH_DAY))
        .format(DateTimeFormatter.ofPattern(DateFormat.SHORT_DATE_MONTH))

fun View.setViewVisibility(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun List<SummaryRoomItem>.totalSelectedMealsPrice(nights: Int): Float {
    return this
        .flatMap { it.meals }
        .filter { it.counter > 0 }
        .sumOf { it.price.amount.toDouble() * it.counter * nights }.toFloat()
}

fun List<SummaryRoomItem>.totalSelectedExtrasPrice(): Float {
    return this
        .flatMap { it.extras }
        .filter { it.selected }
        .sumOf { it.price.amount.toDouble() }.toFloat()
}

fun List<SummaryRoomItem>.getSelectedUpsellItems(upsellItems: List<UpsellItem>): List<UpsellItem> {
    return this
        .flatMap { room -> room.meals.flatMap { meal -> List(meal.counter) { meal.id } } }
        .flatMap { id -> upsellItems.filter { upsellItem -> upsellItem.code() == id } }
}

fun List<SummaryRoomItem>.getSelectedExtrasItems(extrasItems: MutableList<ParcelableExtrasItem>?): List<ParcelableExtrasItem> {
    return this
        .flatMap { room -> room.extras.filter { it.selected }.map { it.id } }
        .flatMap { id -> extrasItems?.filter { extrasItem -> extrasItem.id == id } ?: emptyList() }
}

fun SummaryInput.buildSaveReservationWithAncillariesRequestBody(
    basketReference: String,
    roomList: List<SummaryRoomItem>,
    prevRoomList: List<SummaryRoomItem>?
): SaveReservationWithAncillariesRequestBody {
    return SaveReservationWithAncillariesRequestBody(
        basketReference,
        hotel().code(),
        arrivalDateGQ(),
        departureDateGQ(),
        roomList.toRoomsSelection(),
        prevRoomList.constructPreviousRoomsSelections(roomList)
    )
}

fun List<SummaryRoomItem>.toRoomsSelection(): List<RoomsSelections> {
    return map { room ->
        val packagesSelection = mutableListOf<SelectedPackages>()

        room.meals.filter { it.counter > 0 && it.originalPrice == null }.forEach { meal ->
            packagesSelection.add(SelectedPackages(id = meal.id, noOfSelections = meal.counter))

            if (room.children > 0 && meal.kidsEatFree && !packagesSelection.any{it.id == OPERA_FREE_CHILD_BREAKFAST}) {
                packagesSelection.add(SelectedPackages(id = OPERA_FREE_CHILD_BREAKFAST, noOfSelections = room.children))
            }
        }

        room.extras.filter { it.selected }.forEach { extra ->
            packagesSelection.add(SelectedPackages(id = extra.id, noOfSelections = 1))
        }

        RoomsSelections(packagesSelection = packagesSelection)
    }
}

fun List<SummaryRoomItem>?.constructPreviousRoomsSelections(roomList: List<SummaryRoomItem>): List<PreviousRoomsSelections> {
    return this?.toPrevRoomsSelection()
        ?: roomList.map {
            PreviousRoomsSelections(packagesSelection = emptyList())
        }
}

fun List<SummaryRoomItem>.toPrevRoomsSelection(): List<PreviousRoomsSelections> {
    return map { room ->
        val packagesSelection = mutableListOf<SelectedPackages>()

        room.meals.filter { it.counter > 0 && it.originalPrice == null }.forEach { meal ->
            packagesSelection.add(SelectedPackages(id = meal.id, noOfSelections = meal.counter))

            if (room.children > 0 && meal.kidsEatFree && !packagesSelection.any{it.id == OPERA_FREE_CHILD_BREAKFAST}) {
                packagesSelection.add(SelectedPackages(id = OPERA_FREE_CHILD_BREAKFAST, noOfSelections = room.children))
            }
        }

        room.extras.filter { it.selected }.forEach { extra ->
            packagesSelection.add(SelectedPackages(id = extra.id, noOfSelections = 1))
        }

        PreviousRoomsSelections(packagesSelection = packagesSelection)
    }
}

fun ParcelablePaymentMethodsDetailsInput.toAcceptedCreditCard(): List<AcceptedCreditCard> {
    val listOfAcceptedCard = mutableListOf<AcceptedCreditCard>()
    this.parcelablePaymentMethods.forEach { paymentMethod ->
        paymentMethod.parcelableAcceptedCardTypes?.forEach { card ->
            if(paymentMethod.enabled) {
                listOfAcceptedCard.add(
                    AcceptedCreditCard.builder()
                        .creditCardCode(card.type)
                        .name(card.name)
                        .schemeLogo(card.logoUrl).build()
                )
            }
        }
    }
    return listOfAcceptedCard
}

fun CompanyManagementDetails.toListOfManagementInformationQuestion(): List<ManagementInformationQuestion> {
    val listOfManagementInformationQuestion = mutableListOf<ManagementInformationQuestion>()
    this.let { companyManagementDetails ->
        val purchaseOrderManagement = companyManagementDetails.purchaseOrderManagement
        if (purchaseOrderManagement != null) {
            if (purchaseOrderManagement.isActiveAndPartOfBookingFlow()) {
                listOfManagementInformationQuestion.add(purchaseOrderManagement)
            }
        }
        val customerReferenceManagement = companyManagementDetails.customerReferenceManagement
        if (customerReferenceManagement != null) {
            if (customerReferenceManagement.isActiveAndPartOfBookingFlow()) {
                listOfManagementInformationQuestion.add(customerReferenceManagement)
            }
        }
        val userDefinedManagement = companyManagementDetails.userDefinedManagement
        userDefinedManagement?.let { userDefMg ->
            userDefMg.filterNotNull()
                .filter { it.isActiveAndPartOfBookingFlow()}
                .takeIf { it.isNotEmpty() }
                ?.let { filteredList ->
                    listOfManagementInformationQuestion.addAll(filteredList)
                }
        }

        return listOfManagementInformationQuestion

    }
}

fun ManagementInformationQuestion.isActiveAndPartOfBookingFlow(): Boolean {
    return this.active && this.location == QUESTION_BOOKING_FLOW
}