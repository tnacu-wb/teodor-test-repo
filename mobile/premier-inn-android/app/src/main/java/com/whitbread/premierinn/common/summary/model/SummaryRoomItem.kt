package com.whitbread.premierinn.common.summary.model

import android.os.Parcelable
import com.whitbread.premierinn.data.common.EMPTY_STRING
import kotlinx.parcelize.Parcelize

@Parcelize
data class SummaryRoomItem(
    val roomNumber: String,
    val roomType: String,
    val adults: Int,
    val children: Int,
    val meals: List<SummaryMealItem>,
    val extras: List<SummaryExtrasItem>,
    val roomId: String = EMPTY_STRING
) : Parcelable
