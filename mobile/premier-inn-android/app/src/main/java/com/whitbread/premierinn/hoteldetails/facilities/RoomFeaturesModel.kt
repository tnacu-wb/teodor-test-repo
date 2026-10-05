package com.whitbread.premierinn.hoteldetails.facilities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomFeaturesModel(val drawableId: Int, val title: String): Parcelable
