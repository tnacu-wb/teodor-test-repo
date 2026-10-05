package com.whitbread.premierinn.hoteldetails

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDate

@Parcelize
data class DailyRateInput(val date: LocalDate, val price: PriceParcelable):Parcelable

@Parcelize
data class PriceParcelable(val amount: Float, val currency: String) : Parcelable
