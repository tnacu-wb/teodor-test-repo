package com.whitbread.premierinn.data.model

import com.whitbread.premierinn.data.remote.DashboardApiContract.Room

object DashboardRoomFixture {

    fun aRoom(
            type: String = "DOUBLE"
    ) : Room {
        return Room (
                type
        )
    }
}