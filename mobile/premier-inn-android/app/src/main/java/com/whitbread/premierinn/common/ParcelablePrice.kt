package com.whitbread.premierinn.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelablePrice(val amount: Float, val currency: String) : Parcelable