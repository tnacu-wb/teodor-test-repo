package com.whitbread.premierinn.amend

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.whitbread.premierinn.R
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.utils.StringUtils.LINE_BREAK
import com.whitbread.premierinn.common.utils.guestsAndNights
import com.whitbread.premierinn.common.utils.isAncillariesCloseoutApplicable
import com.whitbread.premierinn.common.utils.sumByFloat
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.ciol.entity.UpsellItemId
import com.whitbread.premierinn.domain.common.AmendRestrictions
import com.whitbread.premierinn.domain.common.DONATION_LIST
import com.whitbread.premierinn.domain.common.FREE_CHILD_BREAKFAST_OPERA_LEGEND1
import com.whitbread.premierinn.domain.common.FREE_CHILD_BREAKFAST_OPERA_LEGEND2
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.HUB_BREAKFAST_CODE
import com.whitbread.premierinn.domain.common.NO_AMOUNT
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomBreakdown
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.common.UpsellAvailable
import com.whitbread.premierinn.domain.common.hoteldetails.entity.AncillaryCloseOutItem
import com.whitbread.premierinn.domain.common.nights
import com.whitbread.premierinn.domain.common.nightsCount
import com.whitbread.premierinn.domain.common.toRoomLeadGuestWithMappedTitle
import com.whitbread.premierinn.domain.graphql.hdp.entity.MealDomain
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.usecase.RemoveStoredRoomUseCase
import org.threeten.bp.LocalDate

data class AmendReservationState(
    val language: String,
    val simplePersistenceManager: SimplePersistenceManager,
    val businessPersistenceManager: BusinessPersistenceManager,
    val isPromotionalBooking: Boolean = false,
    val startAmendResult: AsyncResult<Nothing>? = null,
    val pairOfMealsAndAncillaryCloseoutItems: Pair<List<MealDomain>, List<AncillaryCloseOutItem>>? = null,
    val hotelName: String? = null,
    val galleryImages: List<String>? = null,
    val amendedReservation: AsyncResult<Reservation>? = null,
    private val originalReservation: Reservation? = null,
    val originalBooking: AsyncResult<Booking>? = null,
    val isEciLcoBooking: Boolean = false,
    val isEmployeeBooking: Boolean = false,
    val isBusinessBooking: Boolean = false,
    val cancelBookingInProgress: Boolean? = null,
    val upsellsAvailable: AsyncResult<List<UpsellAvailable>>? = null,
    val amendSummaryInProgress: Boolean = false,
    private val removeRoomFromStorageState: RemoveStoredRoomUseCase.RemoveRoomStorageState? = null
) {
    fun maxNights() : Int {
        return if (isBusinessBooking) {
            businessPersistenceManager.getMaxNightsInnBusiness()
        } else {
            simplePersistenceManager.getMaxNightsLeisure()
        }
    }

    fun maxRooms(): Int {
        return when {
            isBusinessBooking -> {
                businessPersistenceManager.getMaxRoomsInnBusiness()
            }

            isEmployeeBooking -> {
                simplePersistenceManager.getMaxRoomsAmendForEmployee()
            }

            else -> {
                simplePersistenceManager.getMaxRoomsAmend()
            }
        }
    }

    fun maxArrivalDate() : Int {
        return if (isBusinessBooking) {
            businessPersistenceManager.getMaxArrivalDateInnBusiness()
        } else {
            simplePersistenceManager.getMaxArrivalDateLeisure()
        }
    }

    fun addRoomAllowed() =
        !isPromotionalBooking && amendedRoomWithDetails.size < maxRooms() && !isAddRoomRestricted

    fun addRoomEnabled(): Boolean {
        return true
    }

    /**
     * Groups Upsells into dates, and removes x upsell groups for x nights from reservation.
     * Does not compare against dates, but no. days inferred from those dates.
     *
     * @return reduced set of Upsells based on the reservation dates.
     * @param upsells Upsells we want to modify
     * @param reservationDates first: startDate, second: endDate
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun filterUpsellsByDates(
            upsells: List<Upsell>?,
            reservationDates: Pair<LocalDate, LocalDate>?
    ): List<Upsell> {
        val days = reservationDates?.first?.until(reservationDates.second)?.days ?: 0
        val breakfast = upsells?.filter { it.category == Upsell.Category.BREAKFAST }
        val others = upsells?.filter { it.category == Upsell.Category.OTHER }
        val groupedBreakfast = breakfast?.groupBy { it.postingDate }
        val groupedUpsell = others?.groupBy { it.postingDate }
        val breakfastList =
                groupedBreakfast?.values?.take(minOf(days, groupedBreakfast.size))?.flatten()
                        ?.toMutableList()
                        ?: mutableListOf()
        val othersList =
                groupedUpsell?.values?.take(minOf(days, groupedUpsell.size))?.flatten()?.toMutableList()
                        ?: mutableListOf()

        return breakfastList + othersList
    }

    fun getUpsellPrice(): Float {
        return if (amendedReservation is AsyncResult.Success && amendedReservation.data != null) {
            calculateUpsellTotalCost(amendedReservation.data.upsells)
        } else NO_AMOUNT
    }

    fun shouldShowWifiAndMealsWarning(): Boolean {
        // If the number of adults has been reduced for any room the upsells of that room (Wi-Fi and meals) are removed automatically by the API
        // We want to handle this case and show users a warning message about this upsells removal (CTECH-5542)
        return if (amendedReservation is AsyncResult.Success && amendedReservation.data != null && originalReservation != null) {
            amendedReservation.data.roomsCriteria.forEach { roomCriteria ->
                val originalReservationRoomCriteria = originalReservation.roomsCriteria.find { it.roomId == roomCriteria.roomId }
                val isNumberOfAdultsReduced = (originalReservationRoomCriteria?.numberOfAdults ?: 0) > roomCriteria.numberOfAdults

                if (isNumberOfAdultsReduced) {
                    val originalRoomUpsells = originalReservation.upsells.filter { it.roomId == roomCriteria.roomId }
                    val amendedRoomUpsells = amendedReservation.data.upsells.filter { it.roomId == roomCriteria.roomId }

                    val wasWifiRemoved =
                        originalRoomUpsells.any { it.category == Upsell.Category.OTHER && it.code == UpsellItemId.ULTIMATE_WIFI_24_HRS.id } &&
                                amendedRoomUpsells.none { it.category == Upsell.Category.OTHER && it.code == UpsellItemId.ULTIMATE_WIFI_24_HRS.id }

                    return wasWifiRemoved || areMealUpsellsDifferent(originalRoomUpsells, amendedRoomUpsells)
                }
            }
            return false
        } else false
    }

    val isLoading: Boolean
        get() = hotelName == null || startAmendResult is AsyncResult.Loading
                || amendedReservation is AsyncResult.Loading

    val isAncillariesCloseoutApplicable: Boolean
        get() = pairOfMealsAndAncillaryCloseoutItems?.let { pairOfMealsAndAncillaryCloseoutItems ->
            reservationDates?.let { dates ->
                if (pairOfMealsAndAncillaryCloseoutItems.first.isNotEmpty()) {
                    isAncillariesCloseoutApplicable(
                        pairOfMealsAndAncillaryCloseoutItems.second,
                        pairOfMealsAndAncillaryCloseoutItems.first,
                        dates.first,
                        dates.second
                    )
                } else false
            } ?: false
        } ?: false


    val numberOfNights: Int
        get() = if (amendedReservation is AsyncResult.Success && amendedReservation.data != null) {
            reservationDates!!.nightsCount()
        } else 1

    val reservationDates: Pair<LocalDate, LocalDate>?
        get() = if (amendedReservation is AsyncResult.Success && amendedReservation.data != null) {
            amendedReservation.data.arrival to amendedReservation.data.departure
        } else null

    // Add room is never going to be restricted so hardcoding to false
    private val isAddRoomRestricted: Boolean = false

    val amendedRoomWithDetails: List<AmendedRoom>
        get() {
            val roomCriteria: List<RoomCriteria> =
                    if (amendedReservation is AsyncResult.Success && amendedReservation.data != null) {
                        amendedReservation.data.roomsCriteria
                    } else emptyList()
            val roomGuests: List<Guest> =
                    if (amendedReservation is AsyncResult.Success && amendedReservation.data != null) {
                        amendedReservation.data.roomsLeadGuest.toRoomLeadGuestWithMappedTitle(language)
                    } else emptyList()
            val upsellsPerRoom: Map<String, List<Upsell>> =
                    if (amendedReservation is AsyncResult.Success && amendedReservation.data != null) {
                        amendedReservation.data.upsells.groupBy { it.roomId }
                    } else emptyMap()
            val roomBreakdown: List<RoomBreakdown> =
                    if (amendedReservation is AsyncResult.Success && amendedReservation.data != null) {
                        amendedReservation.data.roomsBreakdown
                    } else emptyList()
            val isGuestNamesRestricted: Boolean =
                    if (originalBooking is AsyncResult.Success && originalBooking.data != null) {
                        originalBooking.data.amendRestrictions.restricted && originalBooking.data.amendRestrictions.guestNames
                    } else false
            val isRoomsRestricted: Boolean =
                    if (originalBooking is AsyncResult.Success && originalBooking.data != null) {
                        originalBooking.data.amendRestrictions.restricted && originalBooking.data.amendRestrictions.rooms
                    } else false
            var position = 1
            val removable = roomCriteria.size > 1

            return roomCriteria.zip(roomGuests) { criteria, guest ->
                when {
                    (criteria.roomId != guest.roomId) && !addRoomEnabled() -> throw
                    IllegalArgumentException("RoomCriteria and Guest collections are not in sync")
                    else -> AmendedRoom(
                            position = position++,
                            isRoomRemovable = removable,
                            roomNumber = criteria.roomNumber,
                            roomId = criteria.roomId,
                            roomLeadGuest = guest,
                            roomCriteria = criteria,
                            roomBreakdown = roomBreakdown.firstOrNull { it.roomId == criteria.roomId }
                                ?: RoomBreakdown(PriceDomain(NO_AMOUNT, GBP), criteria.roomId),
                            roomUpsells = filterUpsellsByDates(
                                    upsellsPerRoom[criteria.roomId],
                                    reservationDates
                            ),
                            isRoomRestricted = isRoomsRestricted,
                            isGuestNamesRestricted = isGuestNamesRestricted
                    )
                }
            }
        }

    val getAmendedRoomsTotalCost: Float?
        get() = if (amendedReservation is AsyncResult.Success && amendedReservation.data != null) {
            amendedReservation.data.roomsBreakdown.sumByFloat { it.totalRoomCost.amount }
        } else null

    val getAmendedTotalCost: Float?
        get() = if (amendedReservation is AsyncResult.Success && amendedReservation.data != null &&
            originalReservation != null && getAmendedRoomsTotalCost != null) {
            val donationAmount = originalReservation.upsells.find { it.code in DONATION_LIST }?.unitCost?.amount ?: 0f
            getAmendedRoomsTotalCost!! + (getUpsellPrice() * amendedReservation.data.nights()) + donationAmount
        } else null

    val getOriginalReservation: Boolean?
        get() = if (amendedReservation is AsyncResult.Success && amendedReservation.data != null) {
            true
        } else null

    val getAmendRestrictions: AmendRestrictions?
        get() = if (originalBooking is AsyncResult.Success && originalBooking.data != null) {
            originalBooking.data.amendRestrictions
        } else null

    val originalBookingTotalCost: PriceDomain?
        get() = if (originalBooking is AsyncResult.Success && originalBooking.data != null) {
            originalBooking.data.totalCost
        } else null

    val numberOfAdults: Int
        get() = if (originalBooking is AsyncResult.Success && originalBooking.data != null) {
            originalBooking.data.numberOfAdults
        } else 0

    val numberOfChildren: Int
        get() = if (originalBooking is AsyncResult.Success && originalBooking.data != null) {
            originalBooking.data.numberOfChildren
        } else 0

    val isAmended: Boolean?
        get() = if (amendedReservation is AsyncResult.Success && amendedReservation.data != null && originalReservation != null) {
            if (isEciLcoBooking) {
                true
            } else {
                val orderedListFromDb = sortReservationByRoomId(amendedReservation.data)
                val orderedListFromSp = sortReservationByRoomId(originalReservation)

                orderedListFromDb.arrival != orderedListFromSp.arrival ||
                        orderedListFromDb.departure != orderedListFromSp.departure ||
                        orderedListFromDb.roomsCriteria != orderedListFromSp.roomsCriteria ||
                        orderedListFromDb.roomsLeadGuest != orderedListFromSp.roomsLeadGuest ||
                        orderedListFromDb.roomsBreakdown != orderedListFromSp.roomsBreakdown ||
                        areMealUpsellsDifferent(orderedListFromDb.upsells, orderedListFromSp.upsells)
            }
        } else null

    private fun sortReservationByRoomId(reservationDetails: Reservation): Reservation {
        val orderedListOfRoomCriteria = reservationDetails.roomsCriteria.sortedBy { it.roomId }
        val orderedListOfRoomLeadGuest = reservationDetails.roomsLeadGuest.sortedBy { it.roomId }
        val orderedListOfRoomBreakdown = reservationDetails.roomsBreakdown.sortedBy { it.roomId }
        val orderedListOfUpsells = reservationDetails.upsells.sortedBy { it.roomId }

        return reservationDetails.copy(roomsCriteria = orderedListOfRoomCriteria,
            roomsLeadGuest = orderedListOfRoomLeadGuest, roomsBreakdown = orderedListOfRoomBreakdown,
            upsells = orderedListOfUpsells)
    }

    private fun areMealUpsellsDifferent(upsellsFromDb: List<Upsell>, upsellsFromSp: List<Upsell>): Boolean {
        val upsellsGrpByRoomDb = upsellsFromDb.filter { it.category == Upsell.Category.BREAKFAST }.groupBy { it.roomId }
        val upsellsGrpByRoomSp = upsellsFromSp.filter { it.category == Upsell.Category.BREAKFAST }.groupBy { it.roomId }
        var count = 0
        val size = upsellsGrpByRoomDb.values.size
        // To handle when upsells have been removed i.e. when db value would be empty list
        if (upsellsGrpByRoomDb.isEmpty()) {
            return upsellsGrpByRoomSp.isNotEmpty()
        }
        upsellsGrpByRoomDb.values.forEach { listOfRoomUpsellsDb ->
            val roomUpsellBySetDb = listOfRoomUpsellsDb.toSet()
            upsellsGrpByRoomSp.values.forEach { listOfRoomUpsellsSp ->
                    val roomUpsellBySetSp = listOfRoomUpsellsSp.toSet()
                    if (roomUpsellBySetDb == roomUpsellBySetSp) {
                        count++
                    }
            }
        }

        return size != count
    }

    val hasRoomBeenRemoved =
        removeRoomFromStorageState is RemoveStoredRoomUseCase.RemoveRoomStorageState.Removed

    val getAvailableUpsells: List<UpsellAvailable>?
        get() = if (upsellsAvailable is AsyncResult.Success && upsellsAvailable.data != null) {
            upsellsAvailable.data
        } else null

    data class AmendedRoom(
            val position: Int,
            val isRoomRemovable: Boolean,
            val roomNumber: Int,
            val roomId: String,
            val roomLeadGuest: Guest,
            val roomCriteria: RoomCriteria,
            val roomUpsells: List<Upsell>,
            val roomBreakdown: RoomBreakdown,
            val isRoomRestricted: Boolean,
            val isGuestNamesRestricted: Boolean
    ) {
        fun listOfMealSummary(context: Context, numberOfNights: Int): String {
            val breakfastsGroupedByCode: Map<String, List<Upsell>> = roomUpsells
                    .filter { it.category == Upsell.Category.BREAKFAST ||
                            it.code == HUB_BREAKFAST_CODE ||
                            it.legend == FREE_CHILD_BREAKFAST_OPERA_LEGEND1 ||
                            it.legend == FREE_CHILD_BREAKFAST_OPERA_LEGEND2 }
                    .filter { it.roomId == roomId }.groupBy { it.code }

            return breakfastsGroupedByCode.asIterable().map { mapEntry ->
                val breakfastsGroupedByGuestCount = mapEntry.value.groupBy { it.quantity }

                breakfastsGroupedByGuestCount.asIterable()
                    .map {
                        "${mapEntry.value.first().legend} ${
                            context.getString(
                                R.string.in_parenthesis,
                                guestsAndNights(context, it.key, numberOfNights)
                            )
                        }"
                    }
                    .joinToString(separator = LINE_BREAK) { it }
            }.joinToString(separator = LINE_BREAK) { it }
        }

        fun listOfExtrasSummary(): String {
            val extrasItems = roomUpsells
                .filter { it.category == Upsell.Category.OTHER }
                .filter { it.roomId == roomId }

            return extrasItems.joinToString(separator = LINE_BREAK) { it.legend }
        }
    }
}