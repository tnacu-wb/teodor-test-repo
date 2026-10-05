package com.whitbread.premierinn.domain.reservation

import com.whitbread.premierinn.domain.common.*
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.MealDomain
import com.whitbread.premierinn.domain.reservation.entity.RoomSelectedUpsells
import org.threeten.bp.LocalDate

fun createBreakfast(selectedUpsell: UpsellAvailable?, departure: LocalDate,
                    roomsCriteria: List<RoomCriteria>, originalUpsells: List<Upsell>): List<Upsell> {
    val upsellList = mutableListOf<Upsell>()
    val upsellDates = mutableListOf<LocalDate>()

    upsellDates.add(departure)

    if (selectedUpsell != null) {
        for (room in roomsCriteria) {
            var payingChildren = 0
            val payingAdults = room.numberOfAdults
            if (selectedUpsell.availableForChildren && !selectedUpsell.freeBreakfastOption) {
                payingChildren = room.numberOfChildren
            } else if (room.numberOfChildren != 0) {
                upsellDates.forEach {
                    val kidsFreeBreakfast = Upsell(
                            quantity = room.numberOfChildren,
                            category = Upsell.Category.BREAKFAST,
                            legend = FREE_CHILD_BREAKFAST,
                            postingDate = it,
                            unitCost = selectedUpsell.unitCost.copy(amount = NO_AMOUNT),
                            code = if (selectedUpsell.freeBreakfastCode.isEmpty()) FREE_CHILD_BREAKFAST_CODE else selectedUpsell.freeBreakfastCode,
                            roomId = room.roomId
                    )
                    upsellList.add(kidsFreeBreakfast)
                }
            }

            val payingGuests = payingAdults + payingChildren

            upsellDates.forEach {
                val upsell = Upsell(
                        quantity = payingGuests,
                        category = if (selectedUpsell.code == HUB_BREAKFAST_CODE) Upsell.Category.OTHER else Upsell.Category.BREAKFAST,
                        legend = selectedUpsell.legend,
                        postingDate = it,
                        unitCost = selectedUpsell.unitCost,
                        code = selectedUpsell.code,
                        roomId = room.roomId
                )
                upsellList.add(upsell)
            }
        }
    }

    for (originalUpsell in originalUpsells.filter { it.category == Upsell.Category.OTHER }.filter { it.code != HUB_BREAKFAST_CODE }.groupBy { it.code }) {
        originalUpsell.value.groupBy { it.roomId }.map {  groupByRoomId ->
            groupByRoomId.value.map { upsell ->
                upsellDates.take(groupByRoomId.value.size).forEach { date ->
                    upsellList.add(upsell.copy(postingDate = date))
                }
            }
        }
    }

    return upsellList
}

fun createAmendedUpsells(
    selectedUpsells: List<RoomSelectedUpsells>,
    departure: LocalDate,
    roomsCriteria: List<RoomCriteria>
): List<Upsell> {
    val upsellList = mutableListOf<Upsell>()

    selectedUpsells.forEach { roomSelectedUpsell ->
        for (room in roomsCriteria) {
            if (room.roomId == roomSelectedUpsell.roomId) {
                roomSelectedUpsell.selectedUpsells.forEach { upsell ->
                    if (upsell is MealDomain) {
                        if (room.numberOfChildren != 0 && upsell.freeBreakfastOption == true) {
                            upsellList.add(
                                Upsell(
                                    quantity = room.numberOfChildren,
                                    category = Upsell.Category.BREAKFAST,
                                    legend = upsell.shortDescription ?: EMPTY_STRING_DOMAIN,
                                    postingDate = departure,
                                    unitCost = PriceDomain(
                                        NO_AMOUNT,
                                        upsell.currency ?: GBP_LABEL
                                    ),
                                    code = upsell.freeBreakfastCode ?: OPERA_FREE_CHILD_BREAKFAST,
                                    roomId = room.roomId
                                )
                            )
                        }
                        upsellList.add(
                            Upsell(
                                quantity = upsell.numberOfSelections,
                                category = if (upsell.id == HUB_BREAKFAST_CODE) Upsell.Category.OTHER else Upsell.Category.BREAKFAST,
                                legend = upsell.shortDescription ?: EMPTY_STRING_DOMAIN,
                                postingDate = departure,
                                unitCost = PriceDomain(
                                    upsell.price?.toFloat() ?: 0F,
                                    upsell.currency ?: GBP_LABEL
                                ),
                                code = upsell.id ?: EMPTY_STRING_DOMAIN,
                                roomId = room.roomId
                            )
                        )
                    } else if (upsell is ExtrasItemDomain) {
                        upsellList.add(
                            Upsell(
                                quantity = upsell.numberOfSelections,
                                category = Upsell.Category.OTHER,
                                legend = upsell.name ?: EMPTY_STRING_DOMAIN,
                                postingDate = departure,
                                unitCost = PriceDomain(
                                    upsell.price?.toFloat() ?: 0F,
                                    upsell.currency ?: GBP_LABEL
                                ),
                                code = upsell.id,
                                roomId = room.roomId
                            )
                        )
                    }
                }
            }
        }
    }
    return upsellList
}

fun createRoomsBreakdown(roomList: List<Room>, originalRoomsBreakdown: List<RoomBreakdown>)
        : List<RoomBreakdown> {
    val newRoomsBreakdown = mutableListOf<RoomBreakdown>()

    for ((index, i) in roomList.withIndex()) {
        newRoomsBreakdown.add(originalRoomsBreakdown[index].copy(totalRoomCost = i.cost))
    }

    return newRoomsBreakdown
}

fun createRoomsBreakdown(ratePlanOpera: RatePlanOpera, originalRoomsBreakdown: List<RoomBreakdown>)
        : List<RoomBreakdown> {
    val newRoomsBreakdown = mutableListOf<RoomBreakdown>()
    val stdRoomsList = ratePlanOpera.roomList
    val altRoomsList = ratePlanOpera.alternateRoomList
    val accRoomsList = ratePlanOpera.accessibleRoomList

    if (altRoomsList.isNotEmpty()) {
        // we add the alt rooms
            for ((index, i) in altRoomsList.withIndex()) {
                newRoomsBreakdown.add(originalRoomsBreakdown[index].copy(totalRoomCost = i.cost))
            }
    } else if (accRoomsList.isNullOrEmpty()) {
        // only std present - add them
        for ((index, i) in stdRoomsList.withIndex()) {
            newRoomsBreakdown.add(originalRoomsBreakdown[index].copy(totalRoomCost = i.cost))
        }
    } else {
        // add acc and std
        val listOfGroupedRooms: List<RoomOpera> = stdRoomsList + accRoomsList
        val listOfSortedRooms = listOfGroupedRooms.toMutableList().sortedBy { it.number }

        for ((index, i) in listOfSortedRooms.withIndex()) {
            newRoomsBreakdown.add(originalRoomsBreakdown[index].copy(totalRoomCost = i.cost))
        }
    }

    return newRoomsBreakdown
}