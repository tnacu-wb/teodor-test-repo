package com.whitbread.premierinn.landing.frequentbooking

import com.whitbread.premierinn.data.common.toRoomString
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.dashboard.entity.FrequentBooking

data class FrequentBookingCalendarState(private val roomCriteria: List<RoomCriteria>? = null,
                                        private val frequentBooking: FrequentBooking? = null) {

    val getFrequentBooking: FrequentBooking?
        get() = frequentBooking

    val getRoomCriteria: List<RoomCriteria>
        get() = roomCriteria ?: listOf(RoomCriteria.createWithDefaults())

    val adults : List<Int>
        get() = getRoomCriteria.map { it.numberOfAdults }

    val children : List<Int>
        get() = getRoomCriteria.map { it.numberOfChildren }

    val infants : List<Int>
        get() = getRoomCriteria.map { it.numberOfInfants }

    val cots : List<Boolean>
        get() = getRoomCriteria.map { it.includeCot }

    val roomTypeCodes : List<String>
        get() = getRoomCriteria.mapNotNull { it.roomType.toRoomString() }

}