package com.whitbread.premierinn.amend

import android.os.Parcelable
import com.whitbread.premierinn.common.ParcelablePrice
import com.whitbread.premierinn.domain.common.RoomType
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableRoom(val number: Int, val type: RoomType, val cost: ParcelablePrice,
                          val cityTax: ParcelablePrice?, val lettingType: String) : Parcelable

@Parcelize
data class ParcelableRoomOperaAmend(val number: Int, val type: RoomType, val cost: ParcelablePrice,
                               val cityTax: ParcelablePrice?, val lettingType: String, val numberOfAdults: Int,
                               val numberOfChildren: Int) : Parcelable