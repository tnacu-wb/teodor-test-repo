package com.whitbread.premierinn.domain.common

import com.whitbread.premierinn.domain.booking.entity.LeadBookerAddress

data class ReservationGuest(
    val profileId: String?,
    val reservationId: String,
    val firstName: String,
    val lastName: String,
    val title: String?,
    val email: String?,
    val dateOfBirth: String?,
    val passportNumber: String?,
    val nationality: String?,
    val isAccompanyingGuest: Boolean,
    val address: LeadBookerAddress?
)
