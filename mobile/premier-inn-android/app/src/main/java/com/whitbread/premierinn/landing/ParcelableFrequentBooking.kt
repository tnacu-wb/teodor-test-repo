package com.whitbread.premierinn.landing

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableFrequentBooking(val hotelImage: String,
                                     val hotelName: String,
                                     val hotelCode: String) : Parcelable