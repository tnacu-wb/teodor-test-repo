package com.whitbread.premierinn.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDate

@Parcelize
data class ParcelableDailyRate(val date: LocalDate, val price: ParcelablePrice) : Parcelable