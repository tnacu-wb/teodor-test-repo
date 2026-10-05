package com.whitbread.premierinn.domain.home.entity

import com.whitbread.premierinn.domain.common.RoomCriteria
import org.threeten.bp.LocalDate

data class SelectedHomeScreenCriteria(
        val roomCriteria: List<RoomCriteria>?,
        val searchText: String?,
        val latitude: Float?,
        val longitude: Float?,
        val hotelId: String?,
        val brand: String?,
        val arrival: LocalDate?,
        val departure: LocalDate?
) {
    companion object {
        val EMPTY = SelectedHomeScreenCriteria( null, null, null, null,
                null, null, null, null)
    }
}
