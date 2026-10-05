package com.whitbread.premierinn.data.model

import com.whitbread.premierinn.data.remote.DashboardApiContract.Map

object DashboardMapEntityFixture {

    fun aMap(
            latitude: Double = 	44.601326,
            longitude: Double = 71.424805
    ) : Map  {
        return Map (
                latitude,
                longitude
        )
    }
}