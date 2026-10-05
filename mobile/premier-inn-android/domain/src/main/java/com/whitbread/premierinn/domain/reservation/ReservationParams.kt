package com.whitbread.premierinn.domain.reservation

import org.threeten.bp.LocalDate


data class ReservationParams(val reservationId: String,
                             val arrivalDate: LocalDate,
                             val guestOrBookerSurname: String)