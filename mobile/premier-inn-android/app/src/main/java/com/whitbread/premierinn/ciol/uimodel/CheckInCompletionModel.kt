package com.whitbread.premierinn.ciol.uimodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckInCompletionModel(
    val leadBooker: String,
    val hotelImage: String?,
    val hotelBrand: String,
    val bookingReference: String,
    val hotelId: String
): Parcelable
