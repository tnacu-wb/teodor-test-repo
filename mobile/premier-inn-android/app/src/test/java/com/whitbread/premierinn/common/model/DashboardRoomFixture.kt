package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.domain.dashboard.entity.Room

object DashboardRoomFixture {

    fun aRoom(
            type: String = "DOUBLE"
    ) : Room {
        return Room(
                type
        )
    }
}