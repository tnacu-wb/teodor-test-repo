package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.domain.dashboard.entity.Map

object DashboardMapFixture {

    fun aMap(
            latitude: Double = 	44.601326,
            longitude: Double = 71.424805
    ) : Map {
        return Map(
                latitude,
                longitude
        )
    }
}