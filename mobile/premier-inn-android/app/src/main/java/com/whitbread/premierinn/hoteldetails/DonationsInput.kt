package com.whitbread.premierinn.hoteldetails

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class DonationsInput(
    val maxDonation: Float? = null,
    val minDonation: Float? = null
) : Parcelable