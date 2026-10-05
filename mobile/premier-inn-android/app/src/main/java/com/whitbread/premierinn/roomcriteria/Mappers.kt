package com.whitbread.premierinn.roomcriteria

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.roomcriteria.viewmodel.RoomCriteriaState


fun RoomCriteriaState.toParcelableRooms(): ParcelableRooms {
    return ParcelableRooms(getRooms().map { it.toParcelable() })
}

fun ParcelableRooms.toRoomCriteriaList(): List<RoomCriteria> {
    return rooms.map { it.toRoomCriteria() }
}

fun RoomCriteria.toParcelable(): ParcelableRoomCriteria {
    return ParcelableRoomCriteria(
                numberOfAdults = this.numberOfAdults,
                numberOfChildren = this.numberOfChildren,
                numberOfInfants = this.numberOfInfants,
                includeCot = this.includeCot,
                roomType = this.roomType,
                roomNumber = this.roomNumber,
                roomId = this.roomId)
}

private fun ParcelableRoomCriteria.toRoomCriteria(): RoomCriteria {
    return RoomCriteria(
            numberOfAdults = numberOfAdults,
            numberOfChildren = numberOfChildren,
            numberOfInfants = numberOfInfants,
            includeCot = includeCot,
            roomType = roomType,
            roomNumber = roomNumber,
            roomId = roomId ?: EMPTY_STRING
    )
}