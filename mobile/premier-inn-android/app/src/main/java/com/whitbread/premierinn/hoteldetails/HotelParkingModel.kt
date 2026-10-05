package com.whitbread.premierinn.hoteldetails

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HotelParkingModel(val parkingDescription: List<Content>): Parcelable
