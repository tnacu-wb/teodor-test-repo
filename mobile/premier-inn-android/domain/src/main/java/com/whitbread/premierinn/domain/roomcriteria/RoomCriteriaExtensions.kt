package com.whitbread.premierinn.domain.roomcriteria

import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.RoomType.*

fun RoomCriteria.adultsIncrementAllowed(): Boolean = numberOfAdults.inc() in RoomCriteria.ADULTS_RANGE

fun RoomCriteria.adultsDecrementAllowed(): Boolean = numberOfAdults.dec() in RoomCriteria.ADULTS_RANGE

fun RoomCriteria.childrenIncrementAllowed(): Boolean = numberOfChildren.inc() in RoomCriteria.CHILDREN_RANGE

fun RoomCriteria.childrenDecrementAllowed(): Boolean = numberOfChildren.dec() in RoomCriteria.CHILDREN_RANGE

fun RoomCriteria.infantsIncrementAllowed(): Boolean = numberOfInfants.inc() in RoomCriteria.INFANTS_RANGE

fun RoomCriteria.infantsDecrementAllowed(): Boolean = numberOfInfants.dec() in RoomCriteria.INFANTS_RANGE

fun RoomCriteria.cotAllowed(): Boolean = roomType != ACCESSIBLE

fun RoomCriteria.roomTypeChangeAllowed(type: RoomType) = type in this.compatibleSortedRoomTypes()

fun RoomCriteria.hasInfants(): Boolean = numberOfInfants > 0

fun RoomCriteria.compatibleSortedRoomTypes(): List<RoomType> {
    // NOTE: The order in which these are added to the list is important. This dictates the substitution priority.
    val allRoomTypes = arrayListOf(DOUBLE, SINGLE, TWIN, FAMILY, ACCESSIBLE)
    if (numberOfAdults == 1) {
        allRoomTypes.remove(TWIN)
    } else if (numberOfAdults == 2) {
        allRoomTypes.remove(SINGLE)
    }
    if (numberOfChildren == 0) {
        allRoomTypes.remove(FAMILY)
    }
    if (numberOfChildren > 0) {
        allRoomTypes.remove(TWIN)
        allRoomTypes.remove(DOUBLE)
        allRoomTypes.remove(SINGLE)
        allRoomTypes.remove(ACCESSIBLE)
    }
    if (includeCot) {
        allRoomTypes.remove(TWIN)
        allRoomTypes.remove(SINGLE)
        allRoomTypes.remove(ACCESSIBLE)
    }
    return allRoomTypes
}