package com.whitbread.premierinn.domain.common.model

import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType

object RoomCriteriaFixture {

    fun aRoomCriteria(
            numberOfAdults: Int = 1,
            numberOfChildren: Int = 0,
            numberOfInfants: Int = 0,
            includeCot: Boolean = false,
            roomType: RoomType = RoomType.DOUBLE,
            roomNumber: Int = 1,
            roomId: String = "") : RoomCriteria {
        return RoomCriteria(
                numberOfAdults,
                numberOfChildren,
                numberOfInfants,
                includeCot,
                roomType,
                roomNumber,
                roomId
        )
    }
}