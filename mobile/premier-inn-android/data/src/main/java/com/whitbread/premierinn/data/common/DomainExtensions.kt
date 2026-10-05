package com.whitbread.premierinn.data.common

import com.whitbread.premierinn.domain.common.RoomCriteria

fun List<RoomCriteria>.adultsPerRoomCommaSeparatedList(): String {
    return this.asIterable().joinToString(separator = ",", transform = { it.numberOfAdults.toString() })
}

fun List<RoomCriteria>.childrenPerRoomCommaSeparatedList(): String {
    return this.asIterable().joinToString(separator = ",", transform = { it.numberOfChildren.toString() })
}

fun List<RoomCriteria>.cotPerRoomCommaSeparatedList(): String {
    return this.asIterable().joinToString(separator = ",", transform = { it.includeCot.toString() })
}

fun List<RoomCriteria>.roomTypesPerRoomCommaSeparatedList(): String {
    return this.asSequence().asIterable().map { it.roomType.toRoomString() }.joinToString(separator = ",")
}