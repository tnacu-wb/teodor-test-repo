package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.common.utils.StringUtils
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
            roomId: String = StringUtils.EMPTY_STRING) : RoomCriteria {
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

    fun aSecondRoomCriteria(
            numberOfAdults: Int = 1,
            numberOfChildren: Int = 1,
            numberOfInfants: Int = 0,
            includeCot: Boolean = false,
            roomType: RoomType = RoomType.FAMILY,
            roomNumber: Int = 2,
            roomId: String = "FAM1"): RoomCriteria {
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