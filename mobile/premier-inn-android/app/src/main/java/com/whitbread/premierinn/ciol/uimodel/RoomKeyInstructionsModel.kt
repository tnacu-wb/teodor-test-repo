package com.whitbread.premierinn.ciol.uimodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomKeyInstructionsModel(
    val hotelImage: String?,
    val contentTitle: String,
    val roomKeyInstructions: String,
    val bookingReference: String,
    val hotelId: String
): Parcelable
