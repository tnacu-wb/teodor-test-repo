package com.whitbread.premierinn.ciol.entity

import java.io.Serializable

data class GuestRoomClicked(
    val roomId: String,
    val guestType: Serializable,
    val guestTitle: String,
    val guestFirstName: String,
    val guestLastName: String,
    val nationality: String? = null,
    val passportNumber: String? = null,
)
