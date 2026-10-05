package com.whitbread.premierinn.domain.reservation.entity

import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.RoomBreakdown
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.Upsell
import org.threeten.bp.LocalDate

data class Reservation(val bookingReference: String,
                       val arrival: LocalDate,
                       val departure: LocalDate,
                       val hotelCode: String,
                       val roomsCriteria: List<RoomCriteria>,
                       val roomsLeadGuest: List<Guest>,
                       val roomsBreakdown: List<RoomBreakdown>,
                       val cancelable: Boolean,
                       val upsells: List<Upsell>)