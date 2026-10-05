package com.whitbread.premierinn.amend

import android.os.Parcelable
import com.whitbread.premierinn.common.ParcelablePrice
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableRatePlan(val code: String, val rateType: String, val totalCost: ParcelablePrice,
                              val cityTax: ParcelablePrice?, val roomList: List<ParcelableRoom> ) : Parcelable